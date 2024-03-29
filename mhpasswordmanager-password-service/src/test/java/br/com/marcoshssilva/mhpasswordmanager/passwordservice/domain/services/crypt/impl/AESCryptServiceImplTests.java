package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt.impl;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt.CryptService;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt.exceptions.DecryptionException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt.exceptions.EncryptionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class AESCryptServiceImplTests {

    @Autowired
    @Qualifier("aesCryptService")
    CryptService cryptService;

    static final String secretKey = "IN33dAP@ssw0rd#$";

    @DisplayName("Test if can convert String into bytes and encrypt and decrypt")
    @Test
    void mustEncryptAndDecryptData() {
        final String message = "Helo àáòó;;/21415678!@#!%@¨&*()";
        assertDoesNotThrow(() -> {
            byte[] encrypt = cryptService.encrypt(cryptService.convertStringToByte(message), secretKey);
            byte[] decrypt = cryptService.decrypt(encrypt, secretKey);
            assertEquals(message, cryptService.convertByteToString(decrypt));
        });
    }

    @DisplayName("Test if can using an null secret, throw an EncryptionException")
    @Test
    void mustThrowAndEncryptionException() {
        final String message = "Helo àáòó;;/21415678!@#!%@¨&*()";
        assertThrows(EncryptionException.class,() -> cryptService.encrypt(cryptService.convertStringToByte(message), null));
    }

    @DisplayName("Test if can using an null secret and null payload, throw an DecryptionException")
    @Test
    void mustThrowAndDecryptionException() {
        assertThrows(DecryptionException.class,() -> cryptService.decrypt(null, null));
    }
}
