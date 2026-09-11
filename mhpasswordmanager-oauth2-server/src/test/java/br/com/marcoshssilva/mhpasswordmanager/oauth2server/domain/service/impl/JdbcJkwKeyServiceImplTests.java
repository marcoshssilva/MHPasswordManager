package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.impl;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.exceptions.JwkLoaderFailException;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.JwkKeyData;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.utils.JwksUtils;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Collection;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JdbcJkwKeyServiceImplTests {

    private JdbcJkwKeyServiceImpl service;
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        DataSource dataSource = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .setName("test_jwk_db_" + System.currentTimeMillis())
                .setScriptEncoding("UTF-8")
                .build();

        jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS oauth2_jwk_keys (uuid varchar(36) NOT NULL PRIMARY KEY, algorithm varchar(10) NOT NULL, base64_private_key text NOT NULL, base64_public_key text NOT NULL, created_at timestamp NOT NULL, deleted_at timestamp, active boolean NOT NULL)");

        service = new JdbcJkwKeyServiceImpl(jdbcTemplate);
    }

    private JwkKeyData createSampleJwkKeyData(UUID uuid, boolean active) throws Exception {
        RSAKey rsaKey = JwksUtils.generateRsa();
        return JwkKeyData.builder()
                .uuid(uuid.toString())
                .algorithm("RSA")
                .active(active)
                .publicKey(new String(Base64.getEncoder().encode(rsaKey.toPublicKey().getEncoded()), StandardCharsets.UTF_8))
                .privateKey(new String(Base64.getEncoder().encode(rsaKey.toPrivateKey().getEncoded()), StandardCharsets.UTF_8))
                .createdAt(LocalDateTime.now())
                .build();
    }

    @DisplayName("Should create JWK key and retrieve it successfully")
    @Test
    void shouldCreateAndGetJwkKey() throws Exception {
        UUID uuid = UUID.randomUUID();
        JwkKeyData keyData = createSampleJwkKeyData(uuid, true);

        JwkKeyData created = service.createJwkKey(keyData);
        assertNotNull(created);
        assertEquals(uuid.toString(), created.getUuid());

        JwkKeyData retrieved = service.getJwkKey(uuid);
        assertNotNull(retrieved);
        assertEquals(uuid.toString(), retrieved.getUuid());
        assertTrue(retrieved.getActive());
    }

    @DisplayName("Should throw JwkLoaderFailException when creating duplicate key")
    @Test
    void shouldThrowWhenCreatingDuplicateKey() throws Exception {
        UUID uuid = UUID.randomUUID();
        JwkKeyData keyData = createSampleJwkKeyData(uuid, false);

        service.createJwkKey(keyData);
        assertThrows(JwkLoaderFailException.class, () -> service.createJwkKey(keyData));
    }

    @DisplayName("Should return null when key not found by uuid")
    @Test
    void shouldReturnNullWhenKeyNotFound() {
        assertNull(service.getJwkKey(UUID.randomUUID()));
    }

    @DisplayName("Should return all not-deleted JWK keys")
    @Test
    void shouldGetAllKeys() throws Exception {
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();
        service.createJwkKey(createSampleJwkKeyData(uuid1, true));
        service.createJwkKey(createSampleJwkKeyData(uuid2, false));

        Collection<JwkKeyData> all = service.getAllKeys();
        assertEquals(2, all.size());
    }

    @DisplayName("Should select JWK key and deactivate others")
    @Test
    void shouldSelectJwkKey() throws Exception {
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();
        service.createJwkKey(createSampleJwkKeyData(uuid1, true));
        service.createJwkKey(createSampleJwkKeyData(uuid2, false));

        service.selectJwkKey(uuid2);

        JwkKeyData key1 = service.getJwkKey(uuid1);
        JwkKeyData key2 = service.getJwkKey(uuid2);

        assertFalse(key1.getActive());
        assertTrue(key2.getActive());
    }

    @DisplayName("Should throw JwkLoaderFailException when selecting non-existent key")
    @Test
    void shouldThrowWhenSelectingNonExistentKey() {
        assertThrows(JwkLoaderFailException.class, () -> service.selectJwkKey(UUID.randomUUID()));
    }

    @DisplayName("Should soft delete JWK key")
    @Test
    void shouldDeleteJwkKey() throws Exception {
        UUID uuid = UUID.randomUUID();
        service.createJwkKey(createSampleJwkKeyData(uuid, true));

        service.deleteJwkKey(uuid);

        Collection<JwkKeyData> all = service.getAllKeys();
        assertTrue(all.isEmpty());
    }

    @DisplayName("Should throw JwkLoaderFailException when deleting non-existent key")
    @Test
    void shouldThrowWhenDeletingNonExistentKey() {
        assertThrows(JwkLoaderFailException.class, () -> service.deleteJwkKey(UUID.randomUUID()));
    }

    @DisplayName("Should get active JWK as Nimbus JWK")
    @Test
    void shouldGetActiveJwkKeyAsJwk() throws Exception {
        UUID uuid = UUID.randomUUID();
        service.createJwkKey(createSampleJwkKeyData(uuid, true));

        JWK jwk = service.getKey();
        assertNotNull(jwk);
        assertEquals(uuid.toString(), jwk.getKeyID());
    }

    @DisplayName("Should throw JwkLoaderFailException when no active key exists")
    @Test
    void shouldThrowWhenNoActiveKeyExists() {
        assertThrows(JwkLoaderFailException.class, () -> service.getKey());
    }
}
