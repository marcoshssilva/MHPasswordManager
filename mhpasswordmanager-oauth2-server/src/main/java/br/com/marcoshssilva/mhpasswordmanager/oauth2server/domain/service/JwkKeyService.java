package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.exceptions.JwkLoaderFailException;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.UUID;

public interface JwkKeyService {
    PublicKey getPublicKey() throws JwkLoaderFailException;
    PrivateKey getPrivateKey() throws JwkLoaderFailException;
    void selectJwkKey(UUID uuid) throws JwkLoaderFailException;
}
