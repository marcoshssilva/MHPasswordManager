package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.security.*;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class RSACryptServiceImplTests {
    static PrivateKey privateKey;
    static PublicKey publicKey;
    static Base64.Encoder encoder = Base64.getEncoder();

    @Autowired
    @Qualifier("rsaCryptService")
    CryptService cryptService;

    @BeforeAll
    public static void beforeTestClass() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        privateKey = keyPair.getPrivate();
        publicKey = keyPair.getPublic();
    }

    @DisplayName("Test if can encrypt and decrypt a message")
    @Test
    public void mustEncodeAndDecodeMessage() {
        final String message = "Some message to encode";
        final String k1 = encoder.encodeToString(privateKey.getEncoded());
        final String k2 = encoder.encodeToString(publicKey.getEncoded());

        assertDoesNotThrow(() -> {
            byte[] bytesCrypt = cryptService.encrypt(message.getBytes(), k2);
            byte[] bytesDecrypt = cryptService.decrypt(bytesCrypt, k1);
            String decryptMessage = new String(bytesDecrypt);
            assertEquals(message, decryptMessage);
        });
    }

    @DisplayName("Must resolve an EncryptionException")
    @Test
    public void mustThrowEncryptionExceptionWhenCallIncorrectKeys() {
        assertThrows(EncryptionException.class, () -> {
            final String message = "Some message to encode";
            cryptService.encrypt(message.getBytes(), "P@ssw0rd");
        });
    }

    @DisplayName("Must resolve an DecryptionException")
    @Test
    public void mustThrowDecryptionExceptionWhenCallIncorrectKeys() {
        assertThrows(DecryptionException.class, () -> {
            final String message = "Some message to encode";
            final String k2 = encoder.encodeToString(publicKey.getEncoded());
            byte[] bytesCrypt = cryptService.encrypt(message.getBytes(), k2);
            cryptService.decrypt(bytesCrypt, "P@ssw0rd");
        });
    }
}
