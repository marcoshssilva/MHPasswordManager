package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.exceptions.JwkLoaderFailException;

import com.nimbusds.jose.jwk.RSAKey;

import java.security.PrivateKey;
import java.security.PublicKey;

public interface JwkKeyService {
    PublicKey getPublicKey() throws JwkLoaderFailException;
    PrivateKey getPrivateKey() throws JwkLoaderFailException;
    RSAKey getRSAKey() throws JwkLoaderFailException;
}
