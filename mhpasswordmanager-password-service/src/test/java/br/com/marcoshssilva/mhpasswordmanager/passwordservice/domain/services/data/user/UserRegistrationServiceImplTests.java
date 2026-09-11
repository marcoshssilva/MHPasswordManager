package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.RabbitMQMockTestConfiguration;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities.UserBucket;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.repositories.UserBucketRepository;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.exceptions.UserAuthorizationCannotBeLoadedException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.models.UserRegisteredModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.ActiveProfiles;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@Import(RabbitMQMockTestConfiguration.class)
class UserRegistrationServiceImplTests {

    @Autowired
    private UserRegistrationService service;

    @Autowired
    private UserBucketRepository userBucketRepository;

    @DisplayName("Should load authorizations from JWT subject")
    @Test
    void shouldLoadAuthorizationsFromJwtSubject() throws UserAuthorizationCannotBeLoadedException {
        Jwt jwt = new Jwt("token", Instant.EPOCH, Instant.EPOCH.plusSeconds(60), Map.of("alg", "none"), Map.of("sub", "john"));

        UserAuthorizations authorizations = service.getUserAuthorizations(jwt);

        assertEquals("john", authorizations.getUsername());
        assertTrue(authorizations.getRoles().isEmpty());
        assertTrue(authorizations.getProfiles().isEmpty());
    }

    @DisplayName("Should throw UserAuthorizationCannotBeLoadedException when jwt is invalid")
    @Test
    void shouldThrowExceptionWhenJwtIsNull() {
        assertThrows(UserAuthorizationCannotBeLoadedException.class, () -> service.getUserAuthorizations(null));
    }

    @DisplayName("Should get user registration with associated buckets")
    @Test
    void shouldGetUserRegistration() throws Exception {
        UserBucket bucket = new UserBucket();
        bucket.setId("bucket-reg-1");
        bucket.setName("Reg Bucket");
        bucket.setDescription("Test");
        bucket.setOwnerName("alice");
        bucket.setEncodedPublicKey("pubKey");
        bucket.setEncryptedPrivateKeyWithPassword("privKey");
        bucket.setCreatedAt(LocalDateTime.now(Clock.systemUTC()));
        bucket.setLastUpdate(LocalDateTime.now(Clock.systemUTC()));
        userBucketRepository.save(bucket);

        UserRegisteredModel model = service.getUserRegistration("alice");

        assertEquals("alice", model.getOwnerName());
        assertTrue(model.getBuckets().contains("bucket-reg-1"));
    }
}
