package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.impl;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.exceptions.JwkLoaderFailException;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.JwkKeyData;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.utils.JwksUtils;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.spec.ECGenParameterSpec;
import java.util.Base64;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JWKFactoryImplTests {

    private JWKFactoryImpl jwkFactory;

    @BeforeEach
    void setUp() {
        jwkFactory = new JWKFactoryImpl();
    }

    @DisplayName("Should create RSA JWK from JwkKeyData")
    @Test
    void shouldCreateRsaJwk() throws Exception {
        RSAKey rsaKey = JwksUtils.generateRsa();
        String uuid = UUID.randomUUID().toString();

        JwkKeyData keyData = JwkKeyData.builder()
                .uuid(uuid)
                .algorithm("RSA")
                .publicKey(new String(Base64.getEncoder().encode(rsaKey.toPublicKey().getEncoded()), StandardCharsets.UTF_8))
                .privateKey(new String(Base64.getEncoder().encode(rsaKey.toPrivateKey().getEncoded()), StandardCharsets.UTF_8))
                .build();

        JWK jwk = jwkFactory.fromJwkKeyData(keyData);
        assertNotNull(jwk);
        assertEquals(uuid, jwk.getKeyID());
        assertEquals("RSA", jwk.getKeyType().getValue());
    }

    @DisplayName("Should create EC JWK from JwkKeyData")
    @Test
    void shouldCreateEcJwk() throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC");
        kpg.initialize(new ECGenParameterSpec("secp256r1"));
        KeyPair kp = kpg.generateKeyPair();
        String uuid = UUID.randomUUID().toString();

        JwkKeyData keyData = JwkKeyData.builder()
                .uuid(uuid)
                .algorithm("EC")
                .publicKey(new String(Base64.getEncoder().encode(kp.getPublic().getEncoded()), StandardCharsets.UTF_8))
                .privateKey(new String(Base64.getEncoder().encode(kp.getPrivate().getEncoded()), StandardCharsets.UTF_8))
                .build();

        JWK jwk = jwkFactory.fromJwkKeyData(keyData);
        assertNotNull(jwk);
        assertEquals(uuid, jwk.getKeyID());
        assertEquals("EC", jwk.getKeyType().getValue());
    }

    @DisplayName("Should throw JwkLoaderFailException when algorithm is unsupported")
    @Test
    void shouldThrowWhenAlgorithmUnsupported() throws Exception {
        RSAKey rsaKey = JwksUtils.generateRsa();
        String uuid = UUID.randomUUID().toString();

        JwkKeyData keyData = JwkKeyData.builder()
                .uuid(uuid)
                .algorithm("UNSUPPORTED")
                .publicKey(new String(Base64.getEncoder().encode(rsaKey.toPublicKey().getEncoded()), StandardCharsets.UTF_8))
                .privateKey(new String(Base64.getEncoder().encode(rsaKey.toPrivateKey().getEncoded()), StandardCharsets.UTF_8))
                .build();

        assertThrows(JwkLoaderFailException.class, () -> jwkFactory.fromJwkKeyData(keyData));
    }
}
