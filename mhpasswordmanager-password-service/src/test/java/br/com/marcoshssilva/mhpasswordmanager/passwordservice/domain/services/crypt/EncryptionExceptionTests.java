package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public
class EncryptionExceptionTests {
    @Test(expected = EncryptionException.class)
    @DisplayName("Should test if this exception is throwable with his constructor")
    public void testConstructor() {
        String message = "Encryption failed";
        Throwable cause = new RuntimeException("Something went wrong");
        throw new EncryptionException(message, cause);
    }
}
