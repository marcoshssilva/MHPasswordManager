package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserPasswordKeyTypeTests {

    @DisplayName("Test data for getters and setters")
    @Test
    void testUserPasswordKeyTypeFields() {
        // Create a new UserPasswordKeyType
        UserPasswordKeyType userPasswordKeyType = UserPasswordKeyType.builder()
                .description("password")
                .build();

        // Assert that the fields are set correctly
        assertNull(userPasswordKeyType.getId()); // auto-generated ID should be null
        assertEquals("password", userPasswordKeyType.getDescription());
    }

    @DisplayName("Test hashCode and equals methods")
    @Test
    void testEqualsAndHashCode() {
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

    @DisplayName("Test equals()")
    @Test
    void testEquals() {
        UserPasswordKeyType keyType1 = new UserPasswordKeyType(1L, "Description 1");
        UserPasswordKeyType keyType2 = new UserPasswordKeyType(1L, "Description 1");
        UserPasswordKeyType keyType3 = new UserPasswordKeyType(2L, "Description 2");

        assertEquals(keyType1, keyType2);
        assertNotEquals(keyType1, keyType3);
        assertNotEquals(keyType2, keyType3);
        assertNotEquals(keyType1, null);
        assertNotEquals(keyType1, "not a key type");
    }

    @DisplayName("Test toString()")
    @Test
    void testToString() {
        UserPasswordKeyType keyType = new UserPasswordKeyType(1L, "Description 1");
        String expectedString = "UserPasswordKeyType(id=1, description=Description 1)";
        assertEquals(expectedString, keyType.toString());
    }

    @DisplayName("Test getters and setters individual")
    @Test
    public void testGettersAndSetters() {
        UserPasswordKeyType keyType = new UserPasswordKeyType();
        Long id = 1L;
        String description = "Test description";

        keyType.setId(id);
        keyType.setDescription(description);

        assertEquals(id, keyType.getId());
        assertEquals(description, keyType.getDescription());
    }
}
