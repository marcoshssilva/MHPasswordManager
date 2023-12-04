package br.com.marcoshssilva.mhpasswordmanager.oauth2server.utils;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Arrays;
import java.util.Random;

public final class KeyGeneratorUtils {
    private static Random random = null;

    private KeyGeneratorUtils() {
    }

    public static Random getRandom() {
        if (random == null) {
            random = new Random();
        }
        return random;
    }

    public static KeyPair generateRsaKey() {
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

    public static String generateRecoveryCodeToResetPassword() {
        Random random = getRandom();
        int numbers = 9;
        String value = "";

        for (int i = 0; i < numbers; i++) {
            value = value.concat(String.valueOf(random.nextInt(10)));
        }

        return value.replaceAll("([0-9]{3})([0-9]{3})([0-9]{3})", "$1-$2-$3");
    }
}