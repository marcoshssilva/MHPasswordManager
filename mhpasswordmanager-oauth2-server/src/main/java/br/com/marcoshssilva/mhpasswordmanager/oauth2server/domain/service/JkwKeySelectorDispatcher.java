package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.exceptions.JwkLoaderFailException;
import com.nimbusds.jose.jwk.RSAKey;

public interface JkwKeySelectorDispatcher {
    RSAKey getRSAKey() throws JwkLoaderFailException;
}
