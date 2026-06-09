package br.com.marcoshssilva.mhpasswordmanager.oauth2server.utils;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;

public final class KeyGeneratorUtils {
    private static SecureRandom secureRandom = null;
    private KeyGeneratorUtils() {}
    public static SecureRandom getSecureRandom() {
        if (secureRandom == null) {
            secureRandom = new SecureRandom();
        }
        return secureRandom;
    }

    public static KeyPair generateRsaKey() {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048, getSecureRandom());
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        return keyPair;
    }

    public static String generateRecoveryCodeToResetPassword() {
        SecureRandom random = getSecureRandom();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 9; i++) {
            sb.append(random.nextInt(10));
            if (i == 2 || i == 5) {
                sb.append("-");
            }
        }

        return sb.toString();
    }
}
