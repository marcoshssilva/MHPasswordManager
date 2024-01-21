package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt;

import lombok.RequiredArgsConstructor;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

@RequiredArgsConstructor
public class AESCryptServiceImpl implements CryptService {
    public static final String ALGORITHM = "AES";
    public static final String TRANSFORMATION = "AES/GCM/NoPadding";
    public static final int GCM_IV_LENGTH = 12;
    public static final int GCM_TAG_LENGTH = 16;

    @Override
    public byte[] encrypt(byte[] payload, String secret) {
        try {
            SecretKeySpec key = new SecretKeySpec(getSHA256Hash(secret), ALGORITHM);
            Cipher encrypt = Cipher.getInstance(TRANSFORMATION, "SunJCE");
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, new byte[GCM_IV_LENGTH]);
            encrypt.init(Cipher.ENCRYPT_MODE, key, gcmParameterSpec);
            return encrypt.doFinal(payload);
        } catch (Exception e) {
            throw new EncryptionException("Encryption failed", e);
        }
    }

    @Override
    public byte[] decrypt(byte[] payload, String secret) {
        try {
            SecretKeySpec key = new SecretKeySpec(getSHA256Hash(secret), ALGORITHM);
            Cipher decrypt = Cipher.getInstance(TRANSFORMATION, "SunJCE");
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, new byte[GCM_IV_LENGTH]);
            decrypt.init(Cipher.DECRYPT_MODE, key, gcmParameterSpec);
            return decrypt.doFinal(payload);
        } catch (Exception e) {
            throw new DecryptionException("Decryption failed", e);
        }
    }

    private byte[] getFirst16Bytes(String key) throws NoSuchAlgorithmException {
        byte[] hash = getSHA256Hash(key);
        return Arrays.copyOf(hash, 16); // return the first 16 bytes as the IV
    }

    private byte[] getSHA256Hash(String key) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(key.getBytes());
    }
}
