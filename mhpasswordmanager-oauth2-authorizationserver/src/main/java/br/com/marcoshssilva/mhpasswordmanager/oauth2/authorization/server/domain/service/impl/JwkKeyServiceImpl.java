package br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.domain.service.impl;

import br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.domain.exceptions.JwkLoaderFailException;
import br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.domain.service.JwkKeyService;

import com.nimbusds.jose.jwk.RSAKey;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
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
    private static final String UUID = "0107f47a-2263-421c-81bb-10210c9c2e6d";

    @Override
    public PublicKey getPublicKey() throws JwkLoaderFailException {
        try (InputStream resource = new ClassPathResource("/public-key.pem").getInputStream()) {
            String base64PublicKey = new String(resource.readAllBytes())
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
        try (InputStream resource = new ClassPathResource("/private-key.pem").getInputStream()) {
            String base64PrivateKey = new String(resource.readAllBytes())
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
        return new RSAKey.Builder((RSAPublicKey) getPublicKey()).privateKey(getPrivateKey()).keyID(UUID).build();
    }
}
