package br.com.marcoshssilva.mhpasswordmanager.oauth2server.utils;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.exceptions.JwkLoaderFailException;
import com.nimbusds.jose.jwk.RSAKey;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.UUID;

@Slf4j
public final class JwksUtils {
    private JwksUtils() {}

    public static RSAKey generateRsa() {
        KeyPair keyPair = KeyGeneratorUtils.generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        return new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
    }


    public static String getBase64X509PublicKeyFromBytes(byte[] bytes, String algorithm) {
        String content = new String(bytes, StandardCharsets.UTF_8).replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "").replaceAll("\\s+", "");
        try {
            KeyFactory kf = KeyFactory.getInstance(algorithm);
            PublicKey publicKey = kf.generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(content)));
            return new String(Base64.getEncoder().encode(publicKey.getEncoded()), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new JwkLoaderFailException("Invalid public key", e);
        }
    }
    
    public static String getBase64PKCS8PrivateKeyFromBytes(byte[] bytes, String algorithm) {
        String content = new String(bytes, StandardCharsets.UTF_8).replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "").replaceAll("\\s+", "");
        try {
            KeyFactory kf = KeyFactory.getInstance(algorithm);
            PrivateKey privateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(content)));
            return new String(Base64.getEncoder().encode(privateKey.getEncoded()), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new JwkLoaderFailException("Invalid private key", e);
        }
    }
}