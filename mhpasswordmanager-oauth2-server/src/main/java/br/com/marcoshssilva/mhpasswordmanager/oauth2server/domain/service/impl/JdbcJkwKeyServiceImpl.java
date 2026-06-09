package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.impl;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.exceptions.JwkLoaderFailException;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.JwkKeyData;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.JWKFactory;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.JkwKeySelectorDispatcher;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.JwkKeyService;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.mappers.JwkKeyDataMapper;
import com.nimbusds.jose.jwk.JWK;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.*;

@RequiredArgsConstructor
public class JdbcJkwKeyServiceImpl implements JwkKeyService, JkwKeySelectorDispatcher {
    public static final String SELECT_JWK_KEY_QUERY                  = "SELECT base64_public_key, base64_private_key, uuid, algorithm, created_at, deleted_at, active FROM oauth2_jwk_keys WHERE active = true AND deleted_at IS NULL ORDER BY created_at DESC LIMIT 1";
    public static final String SELECT_BY_UUID_JWK_KEY_QUERY          = "SELECT base64_public_key, base64_private_key, uuid, algorithm, created_at, deleted_at, active FROM oauth2_jwk_keys WHERE uuid = ?";
    public static final String SELECT_ALL_JWK_KEY_QUERY              = "SELECT base64_public_key, base64_private_key, uuid, algorithm, created_at, deleted_at, active FROM oauth2_jwk_keys WHERE deleted_at IS NULL ORDER BY created_at DESC";
    public static final String ENABLE_BY_UUID_JWK_KEY_QUERY          = "UPDATE oauth2_jwk_keys SET active = true WHERE uuid = ? AND deleted_at IS NULL";
    public static final String DISABLE_ALL_NOT_DELETED_JWK_KEY_QUERY = "UPDATE oauth2_jwk_keys SET active = false WHERE deleted_at IS NULL";
    public static final String DELETE_BY_UUID_JWK_KEY_QUERY          = "UPDATE oauth2_jwk_keys SET deleted_at = now(), active = false WHERE uuid = ? AND deleted_at IS NULL";
    public static final String CREATE_JWK_KEY_QUERY                  = "INSERT INTO oauth2_jwk_keys (base64_public_key, base64_private_key, uuid, algorithm, created_at, active) VALUES (?, ?, ?, ?, ?, ?)";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void selectJwkKey(UUID uuid) throws JwkLoaderFailException {
        try {
            this.jdbcTemplate.update(DISABLE_ALL_NOT_DELETED_JWK_KEY_QUERY);
            int rowsUpdated = this.jdbcTemplate.update(ENABLE_BY_UUID_JWK_KEY_QUERY, uuid.toString());
            if (rowsUpdated == 0) {
                throw new IllegalArgumentException("an active JWK key not found in database with the provided uuid");
            }
        } catch (Exception e) {
            throw new JwkLoaderFailException(e.getMessage(), e);
        }
    }

    @Override
    public void deleteJwkKey(UUID uuid) throws JwkLoaderFailException {
        try {
            int rowsUpdated = this.jdbcTemplate.update(DELETE_BY_UUID_JWK_KEY_QUERY, uuid.toString());
            if (rowsUpdated == 0) {
                throw new IllegalArgumentException("JWK key not found in database with the provided uuid");
            }
        } catch (Exception e) {
            throw new JwkLoaderFailException(e.getMessage(), e);
        }
    }

    @Override
    public JwkKeyData createJwkKey(JwkKeyData jwkKeyData) throws JwkLoaderFailException {
        try {
            if (Objects.nonNull(getJwkKey(UUID.fromString(jwkKeyData.getUuid())))) {
                throw new IllegalArgumentException("JWK key already exists in database with the provided uuid");
            }
            this.jdbcTemplate.update(CREATE_JWK_KEY_QUERY, jwkKeyData.getPublicKey(), jwkKeyData.getPrivateKey(), jwkKeyData.getUuid(), jwkKeyData.getAlgorithm(), jwkKeyData.getCreatedAt(), jwkKeyData.getActive());
            return jwkKeyData;
        } catch (Exception e) {
            throw new JwkLoaderFailException(e.getMessage(), e);
        }
    }

    @Override
    public JwkKeyData getJwkKey(UUID uuid) throws JwkLoaderFailException {
        try {
            return this.jdbcTemplate.queryForObject(SELECT_BY_UUID_JWK_KEY_QUERY, JwkKeyDataMapper.getInstance(), uuid.toString());
        } catch (EmptyResultDataAccessException e) { // if result not exists
            return null;
        } catch (Exception e) {
            throw new JwkLoaderFailException(e.getMessage(), e);
        }
    }

    @Override
    public Collection<JwkKeyData> getAllKeys() throws JwkLoaderFailException {
        try {
            return this.jdbcTemplate.query(SELECT_ALL_JWK_KEY_QUERY, JwkKeyDataMapper.getInstance());
        } catch (EmptyResultDataAccessException e) { // if result not exists
            return List.of();
        } catch (Exception e) {
            throw new JwkLoaderFailException(e.getMessage(), e);
        }

    }

    @Override
    public JWK getKey() throws JwkLoaderFailException {
        try {
            return JWKFactory.getBuilder().fromJwkKeyData(getActiveJwkKey());
        } catch (Exception e) {
            throw new JwkLoaderFailException(e.getMessage(), e);
        }
    }

    private JwkKeyData getActiveJwkKey() {
        return Optional.ofNullable(this.jdbcTemplate.queryForObject(SELECT_JWK_KEY_QUERY, JwkKeyDataMapper.getInstance())).orElseThrow(() -> new IllegalArgumentException("an active JWK key not found in database"));
    }
}
