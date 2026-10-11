package br.com.marcoshssilva.mhpasswordmanager.userservice.domain.services;

import br.com.marcoshssilva.mhpasswordmanager.userservice.configs.AvatarProperties;
import br.com.marcoshssilva.mhpasswordmanager.userservice.configs.S3StorageProperties;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.repositories.AccountDetailsRepository;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.entities.AccountDetails;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.services.impl.S3BasedAvatarServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.core.sync.RequestBody;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Optional;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

class S3BasedAvatarServiceTest {
    AccountDetailsRepository repository = mock(AccountDetailsRepository.class);
    S3Client s3 = mock(S3Client.class);
    S3StorageProperties s3StorageProperties = new S3StorageProperties();
    AvatarProperties avatarProperties = new AvatarProperties();
    S3BasedAvatarServiceImpl service = new S3BasedAvatarServiceImpl(repository, s3, s3StorageProperties, avatarProperties);
    MockMultipartFile file;

    @BeforeEach
    void setup() throws Exception {
        s3StorageProperties.setBucket("private-avatars");
        avatarProperties.setDefaultPublicBaseUrl("https://gateway.example/");
        var bytes = new ByteArrayOutputStream();
        javax.imageio.ImageIO.write(new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB), "png", bytes);
        file = new MockMultipartFile("file", bytes.toByteArray());
    }

    void accountExists() {
        when(repository.getAccountDetailsByIdUsername("john")).thenReturn(Optional.of(
                AccountDetails.builder().imageUrl("https://old.example/old.png").build()));
    }

    @Test
    void uploadsUniquePngBeforeUpdatingOnlyUrlAndNeverDeletesOldObject() {
        accountExists();
        when(repository.updateImageUrlByUsername(eq("john"), anyString())).thenReturn(1);
        var first = service.upload("john", file);
        var second = service.upload("john", file);
        assertThat(first).isNotEqualTo(second);
        var request = ArgumentCaptor.forClass(PutObjectRequest.class);
        var order = inOrder(s3, repository);
        order.verify(repository).getAccountDetailsByIdUsername("john");
        order.verify(s3).putObject(request.capture(), any(RequestBody.class));
        order.verify(repository).updateImageUrlByUsername("john", first.getAvatarPublicUrl().toString());
        assertThat(request.getValue().key()).matches("avatar/[0-9a-f-]{36}\\.png");
        assertThat(request.getValue().ifNoneMatch()).isEqualTo("*");
        assertThat(request.getValue().contentType()).isEqualTo("image/png");
        assertThat(first.getAvatarPublicUrl().toString()).matches("https://gateway\\.example/mypass-manager/avatars/[0-9a-f-]{36}\\.png");
        verify(s3, times(2)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
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
        verify(repository, never()).updateImageUrlByUsername(anyString(), anyString());
    }

    @Test
    void collisionRetriesWithAnotherUuid() {
        accountExists();
        when(repository.updateImageUrlByUsername(eq("john"), anyString())).thenReturn(1);
        when(s3.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenThrow(S3Exception.builder().statusCode(412).build()).thenReturn(PutObjectResponse.builder().build());
        service.upload("john", file);
        var request = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3, times(2)).putObject(request.capture(), any(RequestBody.class));
        assertThat(request.getAllValues().get(0).key()).isNotEqualTo(request.getAllValues().get(1).key());
    }

    @Test
    void repeatedCollisionsStopAfterThreeAttempts() {
        accountExists();
        when(s3.putObject(any(PutObjectRequest.class), any(RequestBody.class))).thenThrow(S3Exception.builder().statusCode(409).build());
        assertThatThrownBy(() -> service.upload("john", file)).isInstanceOf(ResponseStatusException.class);
        verify(s3, times(3)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        verify(repository, never()).updateImageUrlByUsername(anyString(), anyString());
    }

    @Test
    void databaseFailureDoesNotReturnSuccessOrDeleteObjects() {
        accountExists();
        when(repository.updateImageUrlByUsername(anyString(), anyString())).thenThrow(new IllegalStateException("database unavailable"));
        assertThatThrownBy(() -> service.upload("john", file)).isInstanceOf(IllegalStateException.class);
        verify(s3).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        verifyNoMoreInteractions(s3);
    }
}
