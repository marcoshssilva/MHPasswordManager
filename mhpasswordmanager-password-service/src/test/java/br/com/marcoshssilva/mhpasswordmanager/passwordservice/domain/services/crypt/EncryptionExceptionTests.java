package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class EncryptionExceptionTests {
    @DisplayName("Should test if this exception is throwable with his constructor")
    @Test()
    void testConstructor() {
        String message = "Encryption failed";
        Throwable cause = new RuntimeException("Something went wrong");
        assertThrows(EncryptionException.class, () -> { throw new EncryptionException(message, cause); });
    }
}
