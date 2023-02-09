package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt;

import lombok.RequiredArgsConstructor;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public class AESCryptServiceImpl implements CryptService {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";

    @Override
    public byte[] encrypt(byte[] payload, String secret) {
        try {
            SecretKeySpec key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            Cipher encrypt = Cipher.getInstance(TRANSFORMATION, "SunJCE");
            encrypt.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(generateIV(secret).getBytes(StandardCharsets.UTF_8)));
            return encrypt.doFinal(payload);
        } catch (Exception e) {
            throw new EncryptionException("Encryption failed", e);
        }
    }

    @Override
    public byte[] decrypt(byte[] payload, String secret) {
        try {
            SecretKeySpec key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            Cipher decrypt = Cipher.getInstance(TRANSFORMATION, "SunJCE");
            decrypt.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(generateIV(secret).getBytes(StandardCharsets.UTF_8)));
            return decrypt.doFinal(payload);
        } catch (Exception e) {
            throw new DecryptionException("Decryption failed", e);
        }
    }

    private String generateIV(String secret) {
        return "A".repeat(secret.length());
    }
}
