package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt;

import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public interface RSAUtilService {
    PrivateKey getPrivateFromPKCS8(String privateKeyString) throws InvalidKeySpecException, NoSuchAlgorithmException;
    PublicKey getPublicFromX509(String publicKeyString) throws NoSuchAlgorithmException, InvalidKeySpecException;
    PublicKey getPublic(PrivateKey privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException;
    X509EncodedKeySpec getX509EncodedKeySpec(byte[] payload, boolean isDecoded);
    PKCS8EncodedKeySpec getPKCS8EncodedKeySpec(byte[] payload, boolean isDecoded);
}
