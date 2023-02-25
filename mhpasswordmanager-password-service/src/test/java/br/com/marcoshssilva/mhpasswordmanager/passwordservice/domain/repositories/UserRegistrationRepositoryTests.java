package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.repositories;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities.UserRegistration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
@Transactional
public class UserRegistrationRepositoryTests {
    @Autowired
    private UserRegistrationRepository userRegistrationRepository;

    @Test
    public void testSaveUserRegistration() {
        // Given
        UserRegistration userRegistration = UserRegistration.builder()
                .id(UUID.randomUUID().toString())
                .email("test@test.com")
                .encodedPublicKey("test public key")
                .encryptedPrivateKeyWithPassword("test encrypted private key with password")
                .encryptedPrivateKey0("test encrypted private key 0")
                .encryptedPrivateKey1("test encrypted private key 1")
                .encryptedPrivateKey2("test encrypted private key 2")
                .encryptedPrivateKey3("test encrypted private key 3")
                .encryptedPrivateKey4("test encrypted private key 4")
                .encryptedPrivateKey5("test encrypted private key 5")
                .encryptedPrivateKey6("test encrypted private key 6")
                .encryptedPrivateKey7("test encrypted private key 7")
                .encryptedPrivateKey8("test encrypted private key 8")
                .encryptedPrivateKey9("test encrypted private key 9")
                .build();

        // When
        UserRegistration savedUserRegistration = userRegistrationRepository.save(userRegistration);

        // Then
        assertNotNull(savedUserRegistration.getId());
        assertEquals(userRegistration.getEmail(), savedUserRegistration.getEmail());
        assertEquals(userRegistration.getEncodedPublicKey(), savedUserRegistration.getEncodedPublicKey());
        assertEquals(userRegistration.getEncryptedPrivateKeyWithPassword(), savedUserRegistration.getEncryptedPrivateKeyWithPassword());
        assertEquals(userRegistration.getEncryptedPrivateKey0(), savedUserRegistration.getEncryptedPrivateKey0());
        assertEquals(userRegistration.getEncryptedPrivateKey1(), savedUserRegistration.getEncryptedPrivateKey1());
        assertEquals(userRegistration.getEncryptedPrivateKey2(), savedUserRegistration.getEncryptedPrivateKey2());
        assertEquals(userRegistration.getEncryptedPrivateKey3(), savedUserRegistration.getEncryptedPrivateKey3());
        assertEquals(userRegistration.getEncryptedPrivateKey4(), savedUserRegistration.getEncryptedPrivateKey4());
        assertEquals(userRegistration.getEncryptedPrivateKey5(), savedUserRegistration.getEncryptedPrivateKey5());
        assertEquals(userRegistration.getEncryptedPrivateKey6(), savedUserRegistration.getEncryptedPrivateKey6());
        assertEquals(userRegistration.getEncryptedPrivateKey7(), savedUserRegistration.getEncryptedPrivateKey7());
        assertEquals(userRegistration.getEncryptedPrivateKey8(), savedUserRegistration.getEncryptedPrivateKey8());
        assertEquals(userRegistration.getEncryptedPrivateKey9(), savedUserRegistration.getEncryptedPrivateKey9());
    }

    @Test
    public void testUpdate() {
        // Create a new user registration entity
        UserRegistration userRegistration = UserRegistration.builder()
                .id(UUID.randomUUID().toString())
                .email("test@example.com")
                .encodedPublicKey("public-key")
                .encryptedPrivateKeyWithPassword("private-key-with-password")
                .encryptedPrivateKey0("private-key-0")
                .encryptedPrivateKey1("private-key-1")
                .encryptedPrivateKey2("private-key-2")
                .encryptedPrivateKey3("private-key-3")
                .encryptedPrivateKey4("private-key-4")
                .encryptedPrivateKey5("private-key-5")
                .encryptedPrivateKey6("private-key-6")
                .encryptedPrivateKey7("private-key-7")
                .encryptedPrivateKey8("private-key-8")
                .encryptedPrivateKey9("private-key-9")
                .build();

        // Persist the user registration entity to the database
        UserRegistration savedUserRegistration = userRegistrationRepository.save(userRegistration);

        // Update the user registration entity
        savedUserRegistration.setEmail("updated@example.com");
        userRegistrationRepository.save(savedUserRegistration);

        // Retrieve the user registration entity from the database
        Optional<UserRegistration> retrievedUserRegistration = userRegistrationRepository.findById(userRegistration.getId());

        // Verify that the retrieved user registration entity matches the updated user registration entity
        assertEquals(Boolean.TRUE, retrievedUserRegistration.isPresent());
        assertEquals(savedUserRegistration, retrievedUserRegistration.orElse(UserRegistration.builder().id(UUID.randomUUID().toString()).build()));
    }

    @Test
    public void testDeleteUserRegistration() {
        // Create a new UserRegistration
        UserRegistration userRegistration = UserRegistration.builder()
                .id(UUID.randomUUID().toString())
                .email("test@test.com")
                .encodedPublicKey("public_key")
                .encryptedPrivateKeyWithPassword("private_key_with_password")
                .encryptedPrivateKey0("private_key0")
                .build();

        // Save the UserRegistration to the repository
        userRegistrationRepository.save(userRegistration);

        // Delete the UserRegistration
        userRegistrationRepository.delete(userRegistration);

        // Try to find the deleted UserRegistration
        UserRegistration deletedUserRegistration = userRegistrationRepository.findById(userRegistration.getId()).orElse(null);

        // Assert that the deleted UserRegistration is null
        assertNull(deletedUserRegistration);
    }
}
