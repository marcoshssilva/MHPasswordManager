package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class UserRegistrationTests {
    @DisplayName("Test hashCode and equals methods")
    @Test
    public void testEqualsAndHashCode() {
        // Create two UserRegistration objects with the same id and email
        UserRegistration userRegistration1 = UserRegistration.builder()
                .id("123")
                .email("test@test.com")
                .build();
        UserRegistration userRegistration2 = UserRegistration.builder()
                .id("123")
                .email("test@test.com")
                .build();
        Object object = new Object();

        // Assert that the two objects are equal
        assertEquals(userRegistration1, userRegistration2);

        // Assert that the two objects have the same hash code
        assertEquals(userRegistration1.hashCode(), userRegistration2.hashCode());

        // Assert that two objects has not equals because its not same class
        assertEquals(Boolean.FALSE, userRegistration1.equals(object));

        // Change the id of userRegistration2
        userRegistration2.setId("456");

        // Assert that the two objects are no longer equal
        assertNotEquals(userRegistration1, userRegistration2);

        // Assert that the two objects no longer have the same hash code
        assertNotEquals(userRegistration1.hashCode(), userRegistration2.hashCode());
    }

    @DisplayName("Test data for getters and setters")
    @Test
    public void testUserRegistrationFields() {
        // Create a new UserRegistration
        UserRegistration userRegistration = UserRegistration.builder()
                .email("test@test.com")
                .encodedPublicKey("public_key")
                .encryptedPrivateKeyWithPassword("private_key_with_password")
                .encryptedPrivateKey0("private_key0")
                .encryptedPrivateKey1("private_key1")
                .encryptedPrivateKey2("private_key2")
                .encryptedPrivateKey3("private_key3")
                .encryptedPrivateKey4("private_key4")
                .encryptedPrivateKey5("private_key5")
                .encryptedPrivateKey6("private_key6")
                .encryptedPrivateKey7("private_key7")
                .encryptedPrivateKey8("private_key8")
                .encryptedPrivateKey9("private_key9")
                .build();

        // Assert that the fields are set correctly
        assertEquals("test@test.com", userRegistration.getEmail());
        assertEquals("public_key", userRegistration.getEncodedPublicKey());
        assertEquals("private_key_with_password", userRegistration.getEncryptedPrivateKeyWithPassword());
        assertEquals("private_key0", userRegistration.getEncryptedPrivateKey0());
        assertEquals("private_key1", userRegistration.getEncryptedPrivateKey1());
        assertEquals("private_key2", userRegistration.getEncryptedPrivateKey2());
        assertEquals("private_key3", userRegistration.getEncryptedPrivateKey3());
        assertEquals("private_key4", userRegistration.getEncryptedPrivateKey4());
        assertEquals("private_key5", userRegistration.getEncryptedPrivateKey5());
        assertEquals("private_key6", userRegistration.getEncryptedPrivateKey6());
        assertEquals("private_key7", userRegistration.getEncryptedPrivateKey7());
        assertEquals("private_key8", userRegistration.getEncryptedPrivateKey8());
        assertEquals("private_key9", userRegistration.getEncryptedPrivateKey9());
    }
}
