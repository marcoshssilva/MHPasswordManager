package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

class UserBucketTests {
    private static final LocalDateTime FIXED_DATE = LocalDateTime.of(2026, 1, 1, 0, 0);

    @DisplayName("Test equals methods")
    @Test
    void testEquals() {
        UserBucket userBucket1 = new UserBucket("id1", "name", "description", "publicKey", "privateKey", FIXED_DATE, FIXED_DATE, "owner");
        UserBucket userBucket2 = new UserBucket("id1", "name", "description", "publicKey", "privateKey", FIXED_DATE, FIXED_DATE, "owner");
        UserBucket userBucket3 = new UserBucket("id2", "name", "description", "publicKey", "privateKey", FIXED_DATE, FIXED_DATE, "owner");

        assertEquals(userBucket1, userBucket2);
        assertNotEquals(userBucket1, userBucket3);
    }

    @DisplayName("Test hashCode methods")
    @Test
    void testHashCode() {
        UserBucket userBucket1 = new UserBucket("id1", "name", "description", "publicKey", "privateKey", FIXED_DATE, FIXED_DATE, "owner");
        UserBucket userBucket2 = new UserBucket("id1", "name", "description", "publicKey", "privateKey", FIXED_DATE, FIXED_DATE, "owner");

        assertEquals(userBucket1.hashCode(), userBucket2.hashCode());
    }

    @DisplayName("Test getters and setters")
    @Test
    void testGettersAndSetters() {
        UserBucket userBucket = new UserBucket();

        userBucket.setId("id");
        userBucket.setName("name");
        userBucket.setDescription("description");
        userBucket.setEncodedPublicKey("publicKey");
        userBucket.setEncryptedPrivateKeyWithPassword("privateKey");
        userBucket.setCreatedAt(FIXED_DATE);
        userBucket.setLastUpdate(FIXED_DATE);
        userBucket.setOwnerName("owner");

        assertEquals("id", userBucket.getId());
        assertEquals("name", userBucket.getName());
        assertEquals("description", userBucket.getDescription());
        assertEquals("publicKey", userBucket.getEncodedPublicKey());
        assertEquals("privateKey", userBucket.getEncryptedPrivateKeyWithPassword());
        assertNotNull(userBucket.getCreatedAt());
        assertNotNull(userBucket.getLastUpdate());
        assertEquals("owner", userBucket.getOwnerName());
    }
}
