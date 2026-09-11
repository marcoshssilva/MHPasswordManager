package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.mappers;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.RecoveryPasswordCodeRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecoveryPasswordCodeRequestMapperTests {

    @Mock
    private ResultSet rs;

    @DisplayName("Should map row to RecoveryPasswordCodeRequest")
    @Test
    void shouldMapRowToRecoveryPasswordCodeRequest() throws SQLException {
        LocalDate now = LocalDate.now();
        Date sqlDate = Date.valueOf(now);

        when(rs.getString("code")).thenReturn("12345678901");
        when(rs.getString("username")).thenReturn("testuser");
        when(rs.getString("ip_client")).thenReturn("127.0.0.1");
        when(rs.getString("user_agent_client")).thenReturn("Mozilla");
        when(rs.getDate("created_at")).thenReturn(sqlDate);
        when(rs.getDate("expires_at")).thenReturn(sqlDate);
        when(rs.getBoolean("completed")).thenReturn(false);

        RecoveryPasswordCodeRequest result = RecoveryPasswordCodeRequestMapper.getInstance().mapRow(rs, 1);
        assertNotNull(result);
        assertEquals("12345678901", result.getCode());
        assertEquals("testuser", result.getUsername());
        assertEquals("127.0.0.1", result.getIpClient());
        assertEquals("Mozilla", result.getUserAgentClient());
        assertEquals(now, result.getCreatedAt());
        assertEquals(now, result.getExpiresAt());
        assertFalse(result.getCompleted());
    }
}
