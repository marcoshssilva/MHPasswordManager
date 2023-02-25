package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserPasswordKeyTypeTests {
    @Test
    public void testUserPasswordKeyTypeFields() {
        // Create a new UserPasswordKeyType
        UserPasswordKeyType userPasswordKeyType = UserPasswordKeyType.builder()
                .description("password")
                .build();

        // Assert that the fields are set correctly
        assertNull(userPasswordKeyType.getId()); // auto-generated ID should be null
        assertEquals("password", userPasswordKeyType.getDescription());
    }

    @Test
    public void testEqualsAndHashCode() {
        // Create two UserPasswordKeyType objects with the same description
        UserPasswordKeyType keyType1 = UserPasswordKeyType.builder()
                .id(1L)
                .description("password")
                .build();

        UserPasswordKeyType keyType2 = UserPasswordKeyType.builder()
                .id(1L)
                .description("password")
                .build();

        // Create a third UserPasswordKeyType object with a different description
        UserPasswordKeyType keyType3 = UserPasswordKeyType.builder()
                .id(2L)
                .description("pin")
                .build();

        // Test the equals() method
        assertEquals(keyType1, keyType2); // keyType1 should equal keyType2
        assertNotEquals(keyType1, keyType3); // keyType1 should not equal keyType3

        // Test the hashCode() method
        assertEquals(keyType1.hashCode(), keyType2.hashCode()); // hashCode should be the same for keyType1 and keyType2
        assertNotEquals(keyType1.hashCode(), keyType3.hashCode()); // hashCode should be different for keyType1 and keyType3
    }
}
