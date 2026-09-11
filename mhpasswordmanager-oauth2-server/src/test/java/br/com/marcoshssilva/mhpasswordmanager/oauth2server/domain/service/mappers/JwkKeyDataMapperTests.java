package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.mappers;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.JwkKeyData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwkKeyDataMapperTests {

    @Mock
    private ResultSet rs;

    @DisplayName("Should map row to JwkKeyData")
    @Test
    void shouldMapRowToJwkKeyData() throws SQLException {
        LocalDateTime now = LocalDateTime.now();
        Timestamp timestamp = Timestamp.valueOf(now);

        when(rs.getString("base64_private_key")).thenReturn("privKey");
        when(rs.getString("base64_public_key")).thenReturn("pubKey");
        when(rs.getString("uuid")).thenReturn("uuid-123");
        when(rs.getBoolean("active")).thenReturn(true);
        when(rs.getTimestamp("created_at")).thenReturn(timestamp);
        when(rs.getTimestamp("deleted_at")).thenReturn(null);
        when(rs.getString("algorithm")).thenReturn("RSA");

        JwkKeyData result = JwkKeyDataMapper.getInstance().mapRow(rs, 1);
        assertNotNull(result);
        assertEquals("privKey", result.getPrivateKey());
        assertEquals("pubKey", result.getPublicKey());
        assertEquals("uuid-123", result.getUuid());
        assertTrue(result.getActive());
        assertEquals("RSA", result.getAlgorithm());
        assertNull(result.getDeletedAt());
    }
}
