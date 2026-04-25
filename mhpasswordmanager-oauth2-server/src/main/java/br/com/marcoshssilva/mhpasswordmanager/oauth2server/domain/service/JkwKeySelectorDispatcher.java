package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.exceptions.JwkLoaderFailException;
import com.nimbusds.jose.jwk.JWK;

public interface JkwKeySelectorDispatcher {
    JWK getKey() throws JwkLoaderFailException;
}
