package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt.impl;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt.CryptService;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt.exceptions.DecryptionException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt.exceptions.EncryptionException;
import lombok.RequiredArgsConstructor;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@RequiredArgsConstructor
public class AESCryptServiceImpl implements CryptService {
    public static final String ALGORITHM = "AES";
    public static final String TRANSFORMATION = "AES/GCM/NoPadding";
    public static final int GCM_IV_LENGTH = 12;
    public static final int GCM_TAG_LENGTH = 16;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Override
    public byte[] encrypt(byte[] payload, String secret) {
        try {
            SecretKeySpec key = new SecretKeySpec(getSHA256Hash(secret), ALGORITHM);
            Cipher encrypt = Cipher.getInstance(TRANSFORMATION, "SunJCE");
            byte[] iv = new byte[GCM_IV_LENGTH];
            SECURE_RANDOM.nextBytes(iv);
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            encrypt.init(Cipher.ENCRYPT_MODE, key, gcmParameterSpec);

            byte[] cipherText = encrypt.doFinal(payload);
            byte[] encryptedPayload = new byte[GCM_IV_LENGTH + cipherText.length];
            System.arraycopy(iv, 0, encryptedPayload, 0, GCM_IV_LENGTH);
            System.arraycopy(cipherText, 0, encryptedPayload, GCM_IV_LENGTH, cipherText.length);

            return encryptedPayload;
        } catch (Exception e) {
            throw new EncryptionException("Encryption failed", e);
        }
    }

    @Override
    public byte[] decrypt(byte[] payload, String secret) {
        try {
            if (payload == null || payload.length < GCM_IV_LENGTH) {
                throw new IllegalArgumentException("Invalid or corrupted payload.");
            }

            SecretKeySpec key = new SecretKeySpec(getSHA256Hash(secret), ALGORITHM);
            Cipher decrypt = Cipher.getInstance(TRANSFORMATION, "SunJCE");

            byte[] iv = new byte[GCM_IV_LENGTH];
            System.arraycopy(payload, 0, iv, 0, GCM_IV_LENGTH);

            int cipherTextLength = payload.length - GCM_IV_LENGTH;
            byte[] cipherText = new byte[cipherTextLength];
            System.arraycopy(payload, GCM_IV_LENGTH, cipherText, 0, cipherTextLength);

            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            decrypt.init(Cipher.DECRYPT_MODE, key, gcmParameterSpec);

            return decrypt.doFinal(cipherText);
        } catch (Exception e) {
            throw new DecryptionException("Decryption failed", e);
        }
    }

    private byte[] getSHA256Hash(String key) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(key.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }
}
