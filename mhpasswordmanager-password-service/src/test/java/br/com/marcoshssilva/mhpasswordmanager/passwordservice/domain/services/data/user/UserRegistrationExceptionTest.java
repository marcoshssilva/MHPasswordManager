package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserRegistrationExceptionTest {
    @DisplayName("Should test constructor with Message")
    @Test
    public void testConstructorWithMessage() {
        String message = "Test exception message";

        UserRegistrationException exception = new UserRegistrationException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }

    @DisplayName("Should test constructor with Message and Throwable Cause")
    @Test
    void testConstructorWithMessageAndCause() {
        String message = "Test exception message";
        Throwable cause = new RuntimeException("Test cause");

        UserRegistrationException exception = new UserRegistrationException(message, cause);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}
