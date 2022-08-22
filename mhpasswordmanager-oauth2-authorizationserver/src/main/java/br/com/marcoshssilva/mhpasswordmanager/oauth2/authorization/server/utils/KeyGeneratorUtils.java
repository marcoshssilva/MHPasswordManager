package br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.utils;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

public final class KeyGeneratorUtils {

    private KeyGeneratorUtils() {
    }

    static KeyPair generateRsaKey() {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        return keyPair;
    }

}