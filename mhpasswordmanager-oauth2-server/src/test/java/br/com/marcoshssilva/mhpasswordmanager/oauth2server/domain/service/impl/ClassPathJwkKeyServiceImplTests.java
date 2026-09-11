package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.impl;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.exceptions.JwkLoaderFailException;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.JwkKeyData;
import com.nimbusds.jose.jwk.JWK;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Collection;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClassPathJwkKeyServiceImplTests {

    private ClassPathJwkKeyServiceImpl service;
    private static final String DEFAULT_UUID = "0107f47a-2263-421c-81bb-10210c9c2e6d";

    @BeforeEach
    void setUp() {
        service = new ClassPathJwkKeyServiceImpl();
    }

    @DisplayName("Should load public and private key from classpath")
    @Test
    void shouldLoadPublicAndPrivateKey() throws Exception {
        PublicKey publicKey = service.getPublicKey();
        assertNotNull(publicKey);
        assertEquals("RSA", publicKey.getAlgorithm());

        PrivateKey privateKey = service.getPrivateKey();
        assertNotNull(privateKey);
        assertEquals("RSA", privateKey.getAlgorithm());
    }

    @DisplayName("Should get JwkKeyData for default classpath uuid")
    @Test
    void shouldGetJwkKeyForDefaultUuid() throws Exception {
        JwkKeyData keyData = service.getJwkKey(UUID.fromString(DEFAULT_UUID));
        assertNotNull(keyData);
        assertEquals(DEFAULT_UUID, keyData.getUuid());
        assertTrue(keyData.getActive());
        assertEquals("RSA", keyData.getAlgorithm());
    }

    @DisplayName("Should throw JwkLoaderFailException when getting key for unknown uuid")
    @Test
    void shouldThrowWhenGettingKeyForUnknownUuid() {
        assertThrows(JwkLoaderFailException.class, () -> service.getJwkKey(UUID.randomUUID()));
    }

    @DisplayName("Should get all keys containing single default key")
    @Test
    void shouldGetAllKeys() throws Exception {
        Collection<JwkKeyData> all = service.getAllKeys();
        assertEquals(1, all.size());
        assertEquals(DEFAULT_UUID, all.iterator().next().getUuid());
    }

    @DisplayName("Should get JWK instance successfully")
    @Test
    void shouldGetJwkInstance() throws Exception {
        JWK jwk = service.getKey();
        assertNotNull(jwk);
        assertEquals(DEFAULT_UUID, jwk.getKeyID());
    }

    @DisplayName("Should throw unsupported exceptions on mutable operations")
    @Test
    void shouldThrowOnMutableOperations() {
        assertThrows(JwkLoaderFailException.class, () -> service.selectJwkKey(UUID.randomUUID()));
        assertThrows(JwkLoaderFailException.class, () -> service.deleteJwkKey(UUID.randomUUID()));
        assertThrows(JwkLoaderFailException.class, () -> service.createJwkKey(JwkKeyData.builder().build()));
    }
}
