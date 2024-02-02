package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt.impl;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt.CryptService;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt.RSAUtilService;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt.exceptions.DecryptionException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt.exceptions.EncryptionException;
import lombok.RequiredArgsConstructor;

import javax.crypto.Cipher;
import java.security.PrivateKey;
import java.security.PublicKey;

@RequiredArgsConstructor
public class RSACryptServiceImpl implements CryptService {
    private static final String ALGORITHM = "RSA/ECB/OAEPPADDING";
    private final RSAUtilService rsaUtilService;

    @Override
    public byte[] encrypt(byte[] payload, String secret) {
        try {
            PublicKey publicKey = rsaUtilService.getPublicFromX509(secret);
            Cipher encrypt = Cipher.getInstance(ALGORITHM);
            encrypt.init(Cipher.ENCRYPT_MODE, publicKey);
            return encrypt.doFinal(payload);
        } catch (Exception e) {
            throw new EncryptionException("Encryption failed", e);
        }
    }

    @Override
    public byte[] decrypt(byte[] payload, String secret) {
        try {
            PrivateKey privateKey = rsaUtilService.getPrivateFromPKCS8(secret);
            Cipher decrypt = Cipher.getInstance(ALGORITHM);
            decrypt.init(Cipher.DECRYPT_MODE, privateKey);
            return decrypt.doFinal(payload);
        } catch (Exception e) {
            throw new DecryptionException("Decryption failed", e);
        }
    }
}
