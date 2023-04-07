package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.models;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RecoveryKeyDataTest {
    @DisplayName("Should test method equals")
    @Test
    void testEquals() {
        RecoveryKeyData data1 = new RecoveryKeyData("key1", new byte[]{1, 2, 3});
        RecoveryKeyData data2 = new RecoveryKeyData("key1", new byte[]{1, 2, 3});
        RecoveryKeyData data3 = new RecoveryKeyData("key2", new byte[]{1, 2, 3});

        assertEquals(data1, data2);
        assertNotEquals(data1, data3);
    }

    @DisplayName("Should test method hashCode")
    @Test
    void testHashCode() {
        RecoveryKeyData data1 = new RecoveryKeyData("key1", new byte[]{1, 2, 3});
        RecoveryKeyData data2 = new RecoveryKeyData("key1", new byte[]{1, 2, 3});
        RecoveryKeyData data3 = new RecoveryKeyData("key2", new byte[]{1, 2, 3});

        assertEquals(data1.hashCode(), data2.hashCode());
        assertNotEquals(data1.hashCode(), data3.hashCode());
    }

    @DisplayName("Should test method toString")
    @Test
    void testToString() {
        RecoveryKeyData data = new RecoveryKeyData("key1", new byte[]{1, 2, 3});

        String expected = "RecoveryKeyData{key='key1', encryptedData=[1, 2, 3]}";
        assertEquals(expected, data.toString());
    }
}
