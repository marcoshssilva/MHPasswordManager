package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class AESCryptServiceImplTests {

    @Autowired
    @Qualifier("aesCryptService")
    CryptService cryptService;

    static final String secretKey = "IN33dAP@ssw0rd#$";

    @DisplayName("Test if can convert String into bytes and encrypt and decrypt")
    @Test
    public void mustEncryptAndDecryptData() {
        final String message = "Helo àáòó;;/21415678!@#!%@¨&*()";
        assertDoesNotThrow(() -> {
            byte[] encrypt = cryptService.encrypt(cryptService.convertStringToByte(message), secretKey);
            byte[] decrypt = cryptService.decrypt(encrypt, secretKey);
            assertEquals(message, cryptService.convertByteToString(decrypt));
        });
    }
}
