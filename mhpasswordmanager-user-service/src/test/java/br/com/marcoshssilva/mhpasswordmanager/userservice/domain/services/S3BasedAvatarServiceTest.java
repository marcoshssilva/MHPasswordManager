package br.com.marcoshssilva.mhpasswordmanager.userservice.domain.services;

import br.com.marcoshssilva.mhpasswordmanager.userservice.configs.AvatarProperties;
import br.com.marcoshssilva.mhpasswordmanager.userservice.configs.S3StorageProperties;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.repositories.AccountDetailsRepository;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.entities.AccountDetails;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.entities.AccountDetailsPK;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.services.impl.S3BasedAvatarServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.ArgumentCaptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.core.sync.RequestBody;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class S3BasedAvatarServiceTest {
    private static final String OLD_URL = "https://old.example/old.png";
    private static final AccountDetailsPK ACCOUNT_ID = new AccountDetailsPK("john", "john@example.com");

    @Autowired
    AccountDetailsRepository repository;
    @Autowired
    EntityManager entityManager;
    @Autowired
    JdbcTemplate jdbcTemplate;
    S3Client s3 = mock(S3Client.class);
    S3StorageProperties s3StorageProperties = new S3StorageProperties();
    AvatarProperties avatarProperties = new AvatarProperties();
    S3BasedAvatarServiceImpl service;
    MockMultipartFile file;

    @BeforeEach
    void setup() throws Exception {
        s3StorageProperties.setBucket("private-avatars");
        avatarProperties.setDefaultPublicBaseUrl("https://gateway.example/");
        service = new S3BasedAvatarServiceImpl(repository, s3, s3StorageProperties, avatarProperties);
        var bytes = new ByteArrayOutputStream();
        javax.imageio.ImageIO.write(new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB), "png", bytes);
        file = new MockMultipartFile("file", bytes.toByteArray());
    }

    AccountDetails accountExists() {
        var year = 2026;
        var month = Month.JANUARY.getValue();
        var day = 1;
        var hour = 12;
        var minute = 0;

        return repository.saveAndFlush(
                AccountDetails.builder()
                    .id(ACCOUNT_ID)
                    .firstName("John")
                    .lastName("Doe")
                    .verified(true)
                    .verifiedAt(LocalDateTime.of(year, month, day, hour, minute))
                    .imageUrl(OLD_URL)
                    .build());
    }

    AccountDetails persistedAccount() {
        entityManager.clear();
        return repository.findById(ACCOUNT_ID).orElseThrow();
    }

    @Test
    void uploadsUniquePngBeforeUpdatingOnlyUrlAndNeverDeletesOldObject() {
        var original = accountExists();
        var urlsDuringUpload = new ArrayList<String>();
        when(s3.putObject(any(PutObjectRequest.class), any(RequestBody.class))).thenAnswer(invocation -> {
            urlsDuringUpload.add(persistedAccount().getImageUrl());
            return PutObjectResponse.builder().build();
        });
        var first = service.upload("john", file);
        assertThat(persistedAccount().getImageUrl()).isEqualTo(first.getAvatarPublicUrl().toString());
        var second = service.upload("john", file);
        assertThat(first.getAvatarPublicUrl()).isNotEqualTo(second.getAvatarPublicUrl());
        assertThat(urlsDuringUpload).containsExactly(OLD_URL, first.getAvatarPublicUrl().toString());
        var updated = persistedAccount();
        assertThat(updated.getImageUrl()).isEqualTo(second.getAvatarPublicUrl().toString());
        assertThat(updated).usingRecursiveComparison().ignoringFields("imageUrl").isEqualTo(original);
        var request = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3, times(2)).putObject(request.capture(), any(RequestBody.class));
        assertThat(request.getAllValues()).allSatisfy(upload -> {
            assertThat(upload.bucket()).isEqualTo("private-avatars");
            assertThat(upload.key()).matches("avatar/[0-9a-f-]{36}\\.png");
            assertThat(upload.ifNoneMatch()).isEqualTo("*");
            assertThat(upload.contentType()).isEqualTo("image/png");
        });
        assertThat(request.getAllValues()).extracting(PutObjectRequest::key).doesNotHaveDuplicates();
        assertThat(first.getAvatarPublicUrl().toString()).matches("https://gateway\\.example/mypass-manager/avatars/[0-9a-f-]{36}\\.png");
        verifyNoMoreInteractions(s3);
    }

    @Test
    void missingAccountDoesNotUpload() {
        assertThatThrownBy(() -> service.upload("missing", file)).isInstanceOfSatisfying(ResponseStatusException.class,
                e -> assertThat(e.getRawStatusCode()).isEqualTo(404));
        verifyNoInteractions(s3);
    }

    @Test
    void storageFailureDoesNotUpdateReference() {
        accountExists();
        when(s3.putObject(any(PutObjectRequest.class), any(RequestBody.class))).thenThrow(S3Exception.builder().statusCode(503).build());
        assertThatThrownBy(() -> service.upload("john", file)).isInstanceOfSatisfying(ResponseStatusException.class,
                e -> assertThat(e.getRawStatusCode()).isEqualTo(503));
        assertThat(persistedAccount().getImageUrl()).isEqualTo(OLD_URL);
    }

    @Test
    void collisionRetriesWithAnotherUuid() {
        accountExists();
        when(s3.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenThrow(S3Exception.builder().statusCode(412).build()).thenReturn(PutObjectResponse.builder().build());
        var result = service.upload("john", file);
        var request = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3, times(2)).putObject(request.capture(), any(RequestBody.class));
        assertThat(request.getAllValues().get(0).key()).isNotEqualTo(request.getAllValues().get(1).key());
        assertThat(persistedAccount().getImageUrl()).isEqualTo(result.getAvatarPublicUrl().toString());
        assertThat(result.getAvatarPublicUrl().getPath()).endsWith(request.getAllValues().get(1).key().substring("avatar/".length()));
        verifyNoMoreInteractions(s3);
    }

    @Test
    void repeatedCollisionsStopAfterThreeAttempts() {
        accountExists();
        when(s3.putObject(any(PutObjectRequest.class), any(RequestBody.class))).thenThrow(S3Exception.builder().statusCode(409).build());
        assertThatThrownBy(() -> service.upload("john", file)).isInstanceOfSatisfying(ResponseStatusException.class,
                e -> assertThat(e.getRawStatusCode()).isEqualTo(503));
        verify(s3, times(3)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        assertThat(persistedAccount().getImageUrl()).isEqualTo(OLD_URL);
        verifyNoMoreInteractions(s3);
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void databaseFailureDoesNotReturnSuccessOrDeleteObjects() {
        // H2 DDL commits implicitly, so this scenario cleans up explicitly instead of relying on rollback.
        try {
            accountExists();
            jdbcTemplate.execute("ALTER TABLE users_details ADD CONSTRAINT avatar_test_reference_unchanged "
                    + "CHECK (username <> 'john' OR imageurl = 'https://old.example/old.png')");
            assertThatThrownBy(() -> service.upload("john", file))
                    .isInstanceOf(DataIntegrityViolationException.class);
            assertThat(persistedAccount().getImageUrl()).isEqualTo(OLD_URL);
            verify(s3).putObject(any(PutObjectRequest.class), any(RequestBody.class));
            verifyNoMoreInteractions(s3);
        } finally {
            try {
                jdbcTemplate.execute("ALTER TABLE users_details DROP CONSTRAINT IF EXISTS avatar_test_reference_unchanged");
            } finally {
                repository.deleteById(ACCOUNT_ID);
            }
        }
    }
}
