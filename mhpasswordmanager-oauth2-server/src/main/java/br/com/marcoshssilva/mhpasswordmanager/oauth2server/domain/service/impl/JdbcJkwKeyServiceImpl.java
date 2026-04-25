package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.impl;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.exceptions.JwkLoaderFailException;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.JwkKeyData;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.JkwKeySelectorDispatcher;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.JwkKeyService;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.mappers.JwkKeyDataMapper;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class JdbcJkwKeyServiceImpl implements JwkKeyService, JkwKeySelectorDispatcher {
    public static final String SELECT_JWK_KEY_QUERY                  = "SELECT base64_public_key, base64_private_key, uuid, algorithm, created_at, deleted_at, active FROM oauth2_jwk_keys WHERE active = true AND deleted_at IS NULL ORDER BY created_at DESC LIMIT 1";
    public static final String ENABLE_BY_UUID_JWK_KEY_QUERY          = "UPDATE oauth2_jwk_keys SET true = false WHERE uuid = ? AND deleted_at IS NULL";
    public static final String DISABLE_ALL_NOT_DELETED_JWK_KEY_QUERY = "UPDATE oauth2_jwk_keys SET active = false WHERE deleted_at IS NULL";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public PublicKey getPublicKey() {
        try {
            JwkKeyData jwkKeyData = getActiveJwkKey();
            KeyFactory kf = KeyFactory.getInstance(jwkKeyData.getAlgorithm());
            return kf.generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(jwkKeyData.getPublicKey())));
        } catch (Exception e) {
            throw new JwkLoaderFailException(e.getMessage(), e);
        }
    }

    @Override
    public PrivateKey getPrivateKey() {
        try {
            JwkKeyData jwkKeyData = getActiveJwkKey();
            KeyFactory kf = KeyFactory.getInstance(jwkKeyData.getAlgorithm());
            return kf.generatePrivate(new X509EncodedKeySpec(Base64.getDecoder().decode(jwkKeyData.getPrivateKey())));
        } catch (Exception e) {
            throw new JwkLoaderFailException(e.getMessage(), e);
        }
    }

    @Override
    public void selectJwkKey(UUID uuid) throws JwkLoaderFailException {
        try {
            int rowsNotDeleted = this.jdbcTemplate.update(DISABLE_ALL_NOT_DELETED_JWK_KEY_QUERY);
            int rowsUpdated    = this.jdbcTemplate.update(ENABLE_BY_UUID_JWK_KEY_QUERY, uuid.toString());
            if (rowsUpdated == 0) {
                throw new IllegalArgumentException("an active JWK key not found in database with the provided uuid");
            }
        } catch (Exception e) {
            throw new JwkLoaderFailException(e.getMessage(), e);
        }
    }

    @Override
    public JWK getKey() throws JwkLoaderFailException {
        try {
            JwkKeyData jwkKeyData = getActiveJwkKey();
            return new RSAKey.Builder((RSAPublicKey) decodePublicKey(jwkKeyData)).privateKey(decodePrivateKey(jwkKeyData)).keyID(jwkKeyData.getUuid()).build();
        } catch (Exception e) {
            throw new JwkLoaderFailException(e.getMessage(), e);
        }
    }

    private PrivateKey decodePrivateKey(JwkKeyData jwkKeyData) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory kf = KeyFactory.getInstance(jwkKeyData.getAlgorithm());
        return kf.generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(jwkKeyData.getPrivateKey())));
    }

    private PublicKey decodePublicKey(JwkKeyData jwkKeyData) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory kf = KeyFactory.getInstance(jwkKeyData.getAlgorithm());
        return kf.generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(jwkKeyData.getPublicKey())));
    }

    private JwkKeyData getActiveJwkKey() {
        return Optional.ofNullable(this.jdbcTemplate.queryForObject(SELECT_JWK_KEY_QUERY, JwkKeyDataMapper.getInstance())).orElseThrow(() -> new IllegalArgumentException("an active JWK key not found in database"));
    }
}
