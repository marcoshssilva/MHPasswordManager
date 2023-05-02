package br.com.marcoshssilva.mhpasswordmanager.passwordservice.web.data.requests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.validation.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Set;

class RegistrationNewAccountRequestTest {
    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @DisplayName("Should test if create object from NoArgsConstructor")
    @Test
    void testNoArgsConstructor() {
        RegistrationNewAccountRequest request = new RegistrationNewAccountRequest();
        assertNotNull(request);
    }

    @DisplayName("Should test if create object from AllArgsConstructor")
    @Test
    void testAllArgsConstructor() {
        String privateKey = "1234567890";
        RegistrationNewAccountRequest request = new RegistrationNewAccountRequest(privateKey);
        assertNotNull(request);
        assertEquals(privateKey, request.getSecret());
    }

    @DisplayName("Should test getters and setters")
    @Test
    void testGettersAndSetters() {
        String privateKey = "1234567890";
        RegistrationNewAccountRequest request = new RegistrationNewAccountRequest();
        request.setSecret(privateKey);
        assertEquals(privateKey, request.getSecret());
    }

    @DisplayName("Should test if Builder works")
    @Test
    void testBuilder() {
        String privateKey = "1234567890";
        RegistrationNewAccountRequest request = RegistrationNewAccountRequest.builder()
                .secret(privateKey)
                .build();
        assertNotNull(request);
        assertEquals(privateKey, request.getSecret());
    }

    @DisplayName("Should invoke a ToString")
    @Test
    void testToString() {
        String privateKey = "1234567890";
        RegistrationNewAccountRequest request = new RegistrationNewAccountRequest(privateKey);
        String expectedString = "RegistrationNewAccountRequest(secret=1234567890)";
        assertEquals(expectedString, request.toString());
    }

    @DisplayName("Should invoke 2 constraint violations: @Size and @NotBlank")
    @Test
    void testPrivateKeyNotBlank() {
        String privateKey = "";
        RegistrationNewAccountRequest request = new RegistrationNewAccountRequest(privateKey);
        Set<ConstraintViolation<RegistrationNewAccountRequest>> validated = validator.validate(request);
        assertEquals(2, validated.size());
    }

    @DisplayName("Should invoke 1 constraint violations: @Size")
    @Test
    void testPrivateKeySize() {
        String privateKey = "123456789";
        RegistrationNewAccountRequest request = new RegistrationNewAccountRequest(privateKey);
        Set<ConstraintViolation<RegistrationNewAccountRequest>> validated = validator.validate(request);
        assertEquals(1, validated.size());
    }
}
