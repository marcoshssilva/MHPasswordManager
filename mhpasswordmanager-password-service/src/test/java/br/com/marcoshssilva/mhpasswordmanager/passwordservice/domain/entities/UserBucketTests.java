package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

class UserBucketTests {
    @Test
    void testEquals() {
        UserBucket userBucket1 = new UserBucket("id1", "name", "description", "publicKey", "privateKey", new Date(), new Date(), "owner");
        UserBucket userBucket2 = new UserBucket("id1", "name", "description", "publicKey", "privateKey", new Date(), new Date(), "owner");
        UserBucket userBucket3 = new UserBucket("id2", "name", "description", "publicKey", "privateKey", new Date(), new Date(), "owner");

        assertEquals(userBucket1, userBucket2);
        assertNotEquals(userBucket1, userBucket3);
    }

    @Test
    void testHashCode() {
        UserBucket userBucket1 = new UserBucket("id1", "name", "description", "publicKey", "privateKey", new Date(), new Date(), "owner");
        UserBucket userBucket2 = new UserBucket("id1", "name", "description", "publicKey", "privateKey", new Date(), new Date(), "owner");

        assertEquals(userBucket1.hashCode(), userBucket2.hashCode());
    }

    @Test
    void testGettersAndSetters() {
        UserBucket userBucket = new UserBucket();

        userBucket.setId("id");
        userBucket.setName("name");
        userBucket.setDescription("description");
        userBucket.setEncodedPublicKey("publicKey");
        userBucket.setEncryptedPrivateKeyWithPassword("privateKey");
        userBucket.setCreatedAt(new Date());
        userBucket.setLastUpdate(new Date());
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
