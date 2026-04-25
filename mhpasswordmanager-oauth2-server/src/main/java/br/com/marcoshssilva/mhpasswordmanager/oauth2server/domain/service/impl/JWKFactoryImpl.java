package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.impl;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.exceptions.JwkLoaderFailException;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.JwkKeyData;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.JWKFactory;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
@Primary
public class JWKFactoryImpl implements JWKFactory {
    @Override
    public JWK fromJwkKeyData(JwkKeyData jwkKeyData) {
        try {
            KeyFactory kf = KeyFactory.getInstance(jwkKeyData.getAlgorithm());
            PrivateKey privateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(jwkKeyData.getPrivateKey())));
            PublicKey  publicKey  = kf.generatePublic( new X509EncodedKeySpec( Base64.getDecoder().decode(jwkKeyData.getPublicKey())));

            return switch (jwkKeyData.getAlgorithm()) {
                case "RSA" -> new RSAKey.Builder((RSAPublicKey) publicKey)
                        .privateKey((RSAPrivateKey) privateKey)
                        .keyID(jwkKeyData.getUuid())
                        .build();
                case "EC" -> new ECKey.Builder(Curve.P_256, (ECPublicKey) publicKey)
                        .privateKey((ECPrivateKey) privateKey)
                        .keyID(jwkKeyData.getUuid())
                        .build();
                default -> throw new UnsupportedOperationException("Unsupported algorithm: " + jwkKeyData.getAlgorithm());
            };
        } catch (Exception e) {
            throw new JwkLoaderFailException(e.getMessage(), e);
        }
    }
}
