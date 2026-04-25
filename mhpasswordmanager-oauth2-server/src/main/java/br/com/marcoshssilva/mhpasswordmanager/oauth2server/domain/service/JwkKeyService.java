package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.exceptions.JwkLoaderFailException;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.JwkKeyData;

import java.util.Collection;
import java.util.UUID;

public interface JwkKeyService {
    void selectJwkKey(UUID uuid) throws JwkLoaderFailException;
    void deleteJwkKey(UUID uuid) throws JwkLoaderFailException;
    JwkKeyData createJwkKey(JwkKeyData jwkKeyData) throws JwkLoaderFailException;
    JwkKeyData getJwkKey(UUID uuid) throws JwkLoaderFailException;
    Collection<JwkKeyData> getAllKeys() throws JwkLoaderFailException;

    default UUID createNotUsedUUID() throws JwkLoaderFailException {
        UUID uuid = null;
        do { uuid = UUID.randomUUID(); } while (getJwkKey(uuid) != null);
        return uuid;
    }
}
