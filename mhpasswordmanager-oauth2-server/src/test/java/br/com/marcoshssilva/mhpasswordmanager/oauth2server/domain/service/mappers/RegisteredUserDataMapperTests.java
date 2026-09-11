package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.mappers;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.RegisteredUserData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisteredUserDataMapperTests {

    @Mock
    private ResultSet rs;

    @DisplayName("Should map row to RegisteredUserData")
    @Test
    void shouldMapRowToRegisteredUserData() throws SQLException {
        when(rs.getString("username")).thenReturn("testuser");
        when(rs.getString("email")).thenReturn("test@example.com");
        when(rs.getString("firstname")).thenReturn("First");
        when(rs.getString("lastname")).thenReturn("Last");
        when(rs.getBoolean("enabled")).thenReturn(true);

        RegisteredUserData result = new RegisteredUserDataMapper().mapRow(rs, 1);
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("First", result.getFirstName());
        assertEquals("Last", result.getLastName());
        assertTrue(result.getIsEnabled());
    }
}
