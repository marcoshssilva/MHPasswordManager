package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt.impl;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt.CryptService;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt.RSAUtilService;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt.exceptions.DecryptionException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt.exceptions.EncryptionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAKey;

@Slf4j
@RequiredArgsConstructor
public class RSACryptServiceImpl implements CryptService {
    private static final String ALGORITHM = "RSA/ECB/OAEPPADDING";
    private static final int OAEP_SHA1_PADDING_LENGTH = 42;
    private final RSAUtilService rsaUtilService;

    @Override
    public byte[] encrypt(byte[] payload, String secret) {
        try {
            PublicKey publicKey = rsaUtilService.getPublicFromX509(secret);
            Cipher encrypt = Cipher.getInstance(ALGORITHM);
            encrypt.init(Cipher.ENCRYPT_MODE, publicKey);
            int maxBlockSize = getRsaKeySizeInBytes((RSAKey) publicKey) - OAEP_SHA1_PADDING_LENGTH;
            return doFinalInBlocks(encrypt, payload, maxBlockSize);
        } catch (Exception e) {
            log.error("Encryption failed", e);
            throw new EncryptionException("Encryption failed", e);
        }
    }

    @Override
    public byte[] decrypt(byte[] payload, String secret) {
        try {
            PrivateKey privateKey = rsaUtilService.getPrivateFromPKCS8(secret);
            Cipher decrypt = Cipher.getInstance(ALGORITHM);
            decrypt.init(Cipher.DECRYPT_MODE, privateKey);
            return doFinalInBlocks(decrypt, payload, getRsaKeySizeInBytes((RSAKey) privateKey));
        } catch (Exception e) {
            throw new DecryptionException("Decryption failed", e);
        }
    }

    private int getRsaKeySizeInBytes(RSAKey key) {
        return (key.getModulus().bitLength() + 7) / 8;
    }

    private byte[] doFinalInBlocks(Cipher cipher, byte[] payload, int blockSize) throws IllegalBlockSizeException, BadPaddingException, IOException {
        if (payload.length == 0) {
            return cipher.doFinal(payload);
        }

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        for (int offset = 0; offset < payload.length; offset += blockSize) {
            int length = Math.min(blockSize, payload.length - offset);
            output.write(cipher.doFinal(payload, offset, length));
        }
        return output.toByteArray();
    }
}
