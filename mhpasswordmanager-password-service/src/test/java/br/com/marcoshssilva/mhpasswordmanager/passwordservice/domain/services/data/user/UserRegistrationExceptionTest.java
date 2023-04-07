package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

public class UserRegistrationExceptionTest {
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
    public void testConstructorWithMessageAndCause() {
        String message = "Test exception message";
        Throwable cause = new RuntimeException("Test cause");

        UserRegistrationException exception = new UserRegistrationException(message, cause);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}
