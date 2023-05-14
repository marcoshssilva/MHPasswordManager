package br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.domain.service.impl;

import br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.configuration.AuthorizationConfigProperties;
import br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.domain.exceptions.JwkLoaderFailException;
import br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.domain.service.JwkKeyService;

import com.nimbusds.jose.jwk.RSAKey;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwkKeyServiceImpl implements JwkKeyService {
    private static final String ALGORITHM = "RSA";
    private final AuthorizationConfigProperties authorizationConfigProperties;

    @Override
    public PublicKey getPublicKey() throws JwkLoaderFailException {
        try {
            String base64PublicKey = authorizationConfigProperties.getJwkPublicKey()
                    .replace("\n", "")
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "");

            KeyFactory kf = KeyFactory.getInstance(ALGORITHM);
            log.info(base64PublicKey);
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(base64PublicKey));
            return kf.generatePublic(x509EncodedKeySpec);
        } catch (Exception e) {
            throw new JwkLoaderFailException(e.getMessage(), e);
        }

    }

    @Override
    public PrivateKey getPrivateKey() throws JwkLoaderFailException {
        try {
            String base64PrivateKey = authorizationConfigProperties.getJwkPrivateKey()
                    .replace("\n", "")
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "");

            KeyFactory kf = KeyFactory.getInstance(ALGORITHM);
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(base64PrivateKey));
            return kf.generatePrivate(pkcs8EncodedKeySpec);
        } catch (Exception e) {
            throw new JwkLoaderFailException(e.getMessage(), e);
        }

    }

    @Override
    public RSAKey getRSAKey() throws JwkLoaderFailException {
        return new RSAKey.Builder((RSAPublicKey) getPublicKey())
                .privateKey(getPrivateKey())
                .keyID(authorizationConfigProperties.getJwkUuid())
                .build();
    }
}
