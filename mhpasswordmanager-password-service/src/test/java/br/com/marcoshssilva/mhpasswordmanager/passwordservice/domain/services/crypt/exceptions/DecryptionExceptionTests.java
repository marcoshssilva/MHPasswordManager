package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt.exceptions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DecryptionExceptionTests {
    @DisplayName("Should test if this exception is throwable with his constructor")
    @Test
    void testConstructor() {
        String message = "Decryption failed";
        Throwable cause = new RuntimeException("Something went wrong");
        assertThrows(DecryptionException.class, () -> { throw new DecryptionException(message, cause); });
    }
}
