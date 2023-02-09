package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt;

import org.springframework.stereotype.Service;
import sun.security.rsa.RSAPrivateCrtKeyImpl;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
public class RSAUtilService {
    public PrivateKey getPrivateFromPKCS8(String privateKeyString) throws Exception {
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec spec = getPKCS8EncodedKeySpec(privateKeyString.getBytes(), false);
        return kf.generatePrivate(spec);
    }

    public PublicKey getPublicFromX509(String publicKeyString) throws Exception {
        KeyFactory kf = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec spec = getX509EncodedKeySpec(publicKeyString.getBytes(), false);
        return kf.generatePublic(spec);
    }

    public PublicKey getPublic(PrivateKey privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        RSAPrivateCrtKeyImpl rsaPrivateKey = (RSAPrivateCrtKeyImpl) privateKey;
        return KeyFactory.getInstance("RSA").generatePublic(new RSAPublicKeySpec(rsaPrivateKey.getModulus(), rsaPrivateKey.getPublicExponent()));
    }

    public X509EncodedKeySpec getX509EncodedKeySpec(byte[] payload, boolean isDecoded) {
        if (isDecoded) {
            return new X509EncodedKeySpec(payload);
        }
        return new X509EncodedKeySpec(Base64.getDecoder().decode(payload));
    }

    public PKCS8EncodedKeySpec getPKCS8EncodedKeySpec(byte[] payload, boolean isDecoded) {
        if (isDecoded) {
            return new PKCS8EncodedKeySpec(payload);
        }
        return new PKCS8EncodedKeySpec(Base64.getDecoder().decode(payload));
    }
}