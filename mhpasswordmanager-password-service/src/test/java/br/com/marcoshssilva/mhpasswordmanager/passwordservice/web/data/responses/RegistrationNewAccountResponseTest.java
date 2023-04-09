package br.com.marcoshssilva.mhpasswordmanager.passwordservice.web.data.responses;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

class RegistrationNewAccountResponseTest {
    @DisplayName("Should invoke a NoArgsConstructor")
    @Test
    void testNoArgsConstructor() {
        RegistrationNewAccountResponse response = new RegistrationNewAccountResponse();
        assertNotNull(response);
    }

    @DisplayName("Should test all getters and setters")
    @Test
    void testGettersAndSetters() {
        String email = "test@example.com";
        Set<String> recoveryCodes = new HashSet<>();
        recoveryCodes.add("abc123");
        recoveryCodes.add("xyz789");

        RegistrationNewAccountResponse response = new RegistrationNewAccountResponse();
        response.setEmail(email);
        response.setRecoveryCodes(recoveryCodes);

        assertEquals(email, response.getEmail());
        assertEquals(recoveryCodes, response.getRecoveryCodes());
    }

    @DisplayName("Should test ToString")
    @Test
    void testToString() {
        String email = "test@example.com";
        Set<String> recoveryCodes = new HashSet<>();
        recoveryCodes.add("abc123");
        recoveryCodes.add("xyz789");

        RegistrationNewAccountResponse response = new RegistrationNewAccountResponse();
        response.setEmail(email);
        response.setRecoveryCodes(recoveryCodes);

        String expectedString = "RegistrationNewAccountResponse(email=test@example.com, recoveryCodes=[abc123, xyz789])";
        assertEquals(expectedString, response.toString());
    }
}
