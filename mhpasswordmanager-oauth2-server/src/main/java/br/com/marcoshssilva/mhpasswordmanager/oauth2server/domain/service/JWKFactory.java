package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.JwkKeyData;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.impl.JWKFactoryImpl;
import com.nimbusds.jose.jwk.JWK;

public interface JWKFactory {
    JWKFactory INSTANCE = new JWKFactoryImpl();

    static JWKFactory getBuilder() {
        return INSTANCE;
    }

    JWK fromJwkKeyData(JwkKeyData jwkKeyData);

}
