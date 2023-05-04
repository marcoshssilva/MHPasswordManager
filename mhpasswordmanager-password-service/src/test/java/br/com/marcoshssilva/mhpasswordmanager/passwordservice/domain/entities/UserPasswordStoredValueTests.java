package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class UserPasswordStoredValueTests {
    private UserPasswordStoredValue userPasswordStoredValue;

    @BeforeEach
    void setUp() {
        userPasswordStoredValue = new UserPasswordStoredValue();
    }

    @DisplayName("Test data for getters and setters")
    @Test
    void testGettersAndSetters() {
        UserPasswordKey key = new UserPasswordKey();
        userPasswordStoredValue.setId(1L);
        userPasswordStoredValue.setKeyId(key);
        userPasswordStoredValue.setData("test data");
        Date now = new Date();
        userPasswordStoredValue.setCreatedAt(now);
        userPasswordStoredValue.setLastUpdate(now);

        assertEquals(1L, userPasswordStoredValue.getId());
        assertEquals(key, userPasswordStoredValue.getKeyId());
        assertEquals("test data", userPasswordStoredValue.getData());
        assertEquals(now, userPasswordStoredValue.getCreatedAt());
        assertEquals(now, userPasswordStoredValue.getLastUpdate());
    }

    @DisplayName("Test equals method")
    @Test
    void testHashCodeAndEquals() {
        UserPasswordKey key = new UserPasswordKey();
        key.setId(1L);

        UserPasswordStoredValue value1 = UserPasswordStoredValue.builder()
                .id(1L)
                .keyId(key)
                .data("data1")
                .createdAt(new Date())
                .lastUpdate(new Date())
                .build();

        UserPasswordStoredValue value2 = UserPasswordStoredValue.builder()
                .id(1L)
                .keyId(key)
                .data("data2")
                .createdAt(new Date())
                .lastUpdate(new Date())
                .build();

        UserPasswordStoredValue value3 = UserPasswordStoredValue.builder()
                .id(2L)
                .keyId(key)
                .data("data1")
                .createdAt(new Date())
                .lastUpdate(new Date())
                .build();

        // Reflexive
        assertTrue(value1.equals(value1));

        // Symmetric
        assertTrue(value1.equals(value2));
        assertTrue(value2.equals(value1));

        // Transitive
        assertFalse(value2.equals(value3));
        assertFalse(value1.equals(value3));

        // Not equal
        assertFalse(value1.equals(null));
        assertFalse(value1.equals("value1"));
        assertFalse(value1.equals(value2.hashCode()));
    }

    @DisplayName("Test hashCode method")
    @Test
    void testHashCode() {
        UserPasswordKey key = new UserPasswordKey();
        key.setId(1L);

        UserPasswordStoredValue value1 = UserPasswordStoredValue.builder()
                .id(1L)
                .keyId(key)
                .data("data1")
                .createdAt(new Date())
                .lastUpdate(new Date())
                .build();

        UserPasswordStoredValue value2 = UserPasswordStoredValue.builder()
                .id(1L)
                .keyId(key)
                .data("data2")
                .createdAt(new Date())
                .lastUpdate(new Date())
                .build();

        // Consistent
        assertEquals(value1.hashCode(), value1.hashCode());

        // Equals objects have the same hashCode
        assertEquals(value1.hashCode(), value2.hashCode());
    }

    @DisplayName("Test toString() method")
    @Test
    void testToString() {
        UserPasswordKey key = new UserPasswordKey();
        key.setId(1L);
        Date date = new Date();

        UserPasswordStoredValue value = UserPasswordStoredValue.builder()
                .id(1L)
                .keyId(key)
                .data("data1")
                .createdAt(date)
                .lastUpdate(date)
                .build();

        String expected = String.format("UserPasswordStoredValue(id=1, keyId=UserPasswordKey(id=1, userRegistration=null, tags=[], description=null, type=null, createdAt=null, lastUpdate=null), data=data1, createdAt=%s, lastUpdate=%s)", date.toString(), date.toString());
        String actual = value.toString();

        assertEquals(expected, actual);
    }
}
