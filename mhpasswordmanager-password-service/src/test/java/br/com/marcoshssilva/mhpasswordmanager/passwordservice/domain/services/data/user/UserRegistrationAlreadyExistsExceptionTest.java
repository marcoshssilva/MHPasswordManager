package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserRegistrationAlreadyExistsExceptionTest {
    @DisplayName("Should test constructor with Message")
    @Test
    public void testConstructorWithMessage() {
        String message = "Test exception message";

        UserRegistrationAlreadyExistsException exception = new UserRegistrationAlreadyExistsException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }

    @DisplayName("Should test constructor with Message and Throwable Cause")
    @Test
    void testConstructorWithMessageAndCause() {
        String message = "Test exception message";
        Throwable cause = new RuntimeException("Test cause");

        UserRegistrationAlreadyExistsException exception = new UserRegistrationAlreadyExistsException(message, cause);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}
