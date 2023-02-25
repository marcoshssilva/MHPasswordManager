package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
public class UserPasswordStoredValueTests {
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

    @DisplayName("Test hashCode and equals methods")
    @Test
    void testHashCodeAndEquals() {
        UserPasswordStoredValue obj1 = new UserPasswordStoredValue();
        obj1.setId(1L);

        UserPasswordStoredValue obj2 = new UserPasswordStoredValue();
        obj2.setId(1L);

        UserPasswordStoredValue obj3 = new UserPasswordStoredValue();
        obj3.setId(2L);

        assertEquals(obj1.hashCode(), obj2.hashCode());
        assertNotEquals(obj1.hashCode(), obj3.hashCode());

        assertEquals(obj1, obj2);
        assertNotEquals(obj1, obj3);
        assertNotEquals(obj2, obj3);
    }
}
