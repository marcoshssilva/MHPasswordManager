package br.com.marcoshssilva.mhpasswordmanager.passwordservice.web.data.requests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.validation.*;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Set;

public class RegistrationNewAccountRequestTest {
    private static Validator validator;

    @BeforeClass
    public static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @DisplayName("Should test if create object from NoArgsConstructor")
    @Test
    public void testNoArgsConstructor() {
        RegistrationNewAccountRequest request = new RegistrationNewAccountRequest();
        assertNotNull(request);
    }

    @DisplayName("Should test if create object from AllArgsConstructor")
    @Test
    public void testAllArgsConstructor() {
        String privateKey = "1234567890";
        RegistrationNewAccountRequest request = new RegistrationNewAccountRequest(privateKey);
        assertNotNull(request);
        assertEquals(privateKey, request.getPrivateKey());
    }

    @DisplayName("Should test getters and setters")
    @Test
    public void testGettersAndSetters() {
        String privateKey = "1234567890";
        RegistrationNewAccountRequest request = new RegistrationNewAccountRequest();
        request.setPrivateKey(privateKey);
        assertEquals(privateKey, request.getPrivateKey());
    }

    @DisplayName("Should test if Builder works")
    @Test
    public void testBuilder() {
        String privateKey = "1234567890";
        RegistrationNewAccountRequest request = RegistrationNewAccountRequest.builder()
                .privateKey(privateKey)
                .build();
        assertNotNull(request);
        assertEquals(privateKey, request.getPrivateKey());
    }

    @DisplayName("Should invoke a ToString")
    @Test
    public void testToString() {
        String privateKey = "1234567890";
        RegistrationNewAccountRequest request = new RegistrationNewAccountRequest(privateKey);
        String expectedString = "RegistrationNewAccountRequest(privateKey=1234567890)";
        assertEquals(expectedString, request.toString());
    }

    @DisplayName("Should invoke 2 constraint violations: @Size and @NotBlank")
    @Test
    public void testPrivateKeyNotBlank() {
        String privateKey = "";
        RegistrationNewAccountRequest request = new RegistrationNewAccountRequest(privateKey);
        Set<ConstraintViolation<RegistrationNewAccountRequest>> validated = validator.validate(request);
        assertEquals(2, validated.size());
    }

    @DisplayName("Should invoke 1 constraint violations: @Size")
    @Test
    public void testPrivateKeySize() {
        String privateKey = "123456789";
        RegistrationNewAccountRequest request = new RegistrationNewAccountRequest(privateKey);
        Set<ConstraintViolation<RegistrationNewAccountRequest>> validated = validator.validate(request);
        assertEquals(1, validated.size());
    }
}
