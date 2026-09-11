package br.com.marcoshssilva.mhpasswordmanager.oauth2server.utils;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.exceptions.JwkLoaderFailException;
import com.nimbusds.jose.jwk.RSAKey;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwksUtilsTests {

    @DisplayName("Should generate valid RSA key")
    @Test
    void shouldGenerateRsaKey() throws Exception {
        RSAKey rsaKey = JwksUtils.generateRsa();
        assertNotNull(rsaKey);
        assertNotNull(rsaKey.getKeyID());
        assertNotNull(rsaKey.toRSAPublicKey());
        assertNotNull(rsaKey.toRSAPrivateKey());
    }

    @DisplayName("Should encode and decode public key from bytes")
    @Test
    void shouldExtractPublicKeyFromBytes() throws Exception {
        RSAKey rsaKey = JwksUtils.generateRsa();
        byte[] pubBytes = Base64.getEncoder().encode(rsaKey.toPublicKey().getEncoded());
        String pem = "-----BEGIN PUBLIC KEY-----\n" + new String(pubBytes, StandardCharsets.UTF_8) + "\n-----END PUBLIC KEY-----";

        String base64Result = JwksUtils.getBase64X509PublicKeyFromBytes(pem.getBytes(StandardCharsets.UTF_8), "RSA");
        assertNotNull(base64Result);
    }

    @DisplayName("Should encode and decode private key from bytes")
    @Test
    void shouldExtractPrivateKeyFromBytes() throws Exception {
        RSAKey rsaKey = JwksUtils.generateRsa();
        byte[] privBytes = Base64.getEncoder().encode(rsaKey.toPrivateKey().getEncoded());
        String pem = "-----BEGIN PRIVATE KEY-----\n" + new String(privBytes, StandardCharsets.UTF_8) + "\n-----END PRIVATE KEY-----";

        String base64Result = JwksUtils.getBase64PKCS8PrivateKeyFromBytes(pem.getBytes(StandardCharsets.UTF_8), "RSA");
        assertNotNull(base64Result);
    }

    @DisplayName("Should throw JwkLoaderFailException when public key bytes are invalid")
    @Test
    void shouldThrowWhenPublicKeyInvalid() {
        assertThrows(JwkLoaderFailException.class, () -> JwksUtils.getBase64X509PublicKeyFromBytes("invalid".getBytes(), "RSA"));
    }

    @DisplayName("Should throw JwkLoaderFailException when private key bytes are invalid")
    @Test
    void shouldThrowWhenPrivateKeyInvalid() {
        assertThrows(JwkLoaderFailException.class, () -> JwksUtils.getBase64PKCS8PrivateKeyFromBytes("invalid".getBytes(), "RSA"));
    }
}
