package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class UserPasswordStoredValueTests {
    private static final LocalDateTime FIXED_DATE = LocalDateTime.of(2026, Month.JANUARY, 1, 0, 0);

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
        userPasswordStoredValue.setCreatedAt(FIXED_DATE);
        userPasswordStoredValue.setLastUpdate(FIXED_DATE);

        assertEquals(1L, userPasswordStoredValue.getId());
        assertEquals(key, userPasswordStoredValue.getKeyId());
        assertEquals("test data", userPasswordStoredValue.getData());
        assertEquals(FIXED_DATE, userPasswordStoredValue.getCreatedAt());
        assertEquals(FIXED_DATE, userPasswordStoredValue.getLastUpdate());
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
                .createdAt(FIXED_DATE)
                .lastUpdate(FIXED_DATE)
                .build();

        UserPasswordStoredValue value2 = UserPasswordStoredValue.builder()
                .id(1L)
                .keyId(key)
                .data("data2")
                .createdAt(FIXED_DATE)
                .lastUpdate(FIXED_DATE)
                .build();

        UserPasswordStoredValue value3 = UserPasswordStoredValue.builder()
                .id(2L)
                .keyId(key)
                .data("data1")
                .createdAt(FIXED_DATE)
                .lastUpdate(FIXED_DATE)
                .build();

        // Reflexive
        assertEquals(value1, value1);

        // Symmetric
        assertEquals(value1, value2);
        assertEquals(value2, value1);

        // Transitive
        assertNotEquals(value2, value3);
        assertNotEquals(value1, value3);

        // Not equal
        assertNotEquals(null, value1);
        assertNotEquals("value1", value1);
        assertNotEquals(value1, value2.hashCode());
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
                .createdAt(FIXED_DATE)
                .lastUpdate(FIXED_DATE)
                .build();

        UserPasswordStoredValue value2 = UserPasswordStoredValue.builder()
                .id(1L)
                .keyId(key)
                .data("data2")
                .createdAt(FIXED_DATE)
                .lastUpdate(FIXED_DATE)
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
        UserPasswordStoredValue value = UserPasswordStoredValue.builder()
                .id(1L)
                .keyId(key)
                .data("data1")
                .createdAt(FIXED_DATE)
                .lastUpdate(FIXED_DATE)
                .build();

        String expected = String.format("UserPasswordStoredValue(id=1, keyId=UserPasswordKey(id=1, userBucket=null, tags=[], description=null, type=null, createdAt=null, lastUpdate=null), data=data1, createdAt=%s, lastUpdate=%s)", FIXED_DATE, FIXED_DATE);
        String actual = value.toString();

        assertEquals(expected, actual);
    }
}
