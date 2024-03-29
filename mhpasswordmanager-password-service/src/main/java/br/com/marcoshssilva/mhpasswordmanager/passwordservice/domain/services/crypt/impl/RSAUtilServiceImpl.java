package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt.impl;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt.RSAUtilService;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
public class RSAUtilServiceImpl implements RSAUtilService {
    private static final String ALGORITHM = "RSA";

    @Override
    public PrivateKey getPrivateFromPKCS8(String privateKeyString) throws InvalidKeySpecException, NoSuchAlgorithmException {
        KeyFactory kf = KeyFactory.getInstance(ALGORITHM);
        PKCS8EncodedKeySpec spec = getPKCS8EncodedKeySpec(privateKeyString.getBytes(), false);
        return kf.generatePrivate(spec);
    }

    @Override
    public PublicKey getPublicFromX509(String publicKeyString) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory kf = KeyFactory.getInstance(ALGORITHM);
        X509EncodedKeySpec spec = getX509EncodedKeySpec(publicKeyString.getBytes(), false);
        return kf.generatePublic(spec);
    }

    @Override
    public PublicKey getPublic(PrivateKey privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        RSAPrivateCrtKey rsaPrivateKey = (RSAPrivateCrtKey) privateKey;
        return KeyFactory.getInstance(ALGORITHM).generatePublic(new RSAPublicKeySpec(rsaPrivateKey.getModulus(), rsaPrivateKey.getPublicExponent()));
    }

    @Override
    public X509EncodedKeySpec getX509EncodedKeySpec(byte[] payload, boolean isDecoded) {
        if (isDecoded) {
            return new X509EncodedKeySpec(payload);
        }
        return new X509EncodedKeySpec(Base64.getDecoder().decode(payload));
    }

    @Override
    public PKCS8EncodedKeySpec getPKCS8EncodedKeySpec(byte[] payload, boolean isDecoded) {
        if (isDecoded) {
            return new PKCS8EncodedKeySpec(payload);
        }
        return new PKCS8EncodedKeySpec(Base64.getDecoder().decode(payload));
    }
}
