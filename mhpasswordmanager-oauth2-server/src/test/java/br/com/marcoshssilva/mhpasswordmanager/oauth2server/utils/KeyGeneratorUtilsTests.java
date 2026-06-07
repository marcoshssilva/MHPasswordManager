package br.com.marcoshssilva.mhpasswordmanager.oauth2server.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.security.SecureRandom;

import static org.junit.jupiter.api.Assertions.*;

class KeyGeneratorUtilsTests {

    @Test
    @DisplayName("Should return a non-null SecureRandom instance")
    void shouldReturnNonNullSecureRandom() {
        SecureRandom secureRandom = KeyGeneratorUtils.getSecureRandom();
        assertNotNull(secureRandom);
    }

    @Test
    @DisplayName("Should return the same SecureRandom instance (Singleton)")
    void shouldReturnSameSecureRandomInstance() {
        SecureRandom first = KeyGeneratorUtils.getSecureRandom();
        SecureRandom second = KeyGeneratorUtils.getSecureRandom();
        assertSame(first, second);
    }

    @Test
    @DisplayName("Should generate a valid RSA KeyPair")
    void shouldGenerateValidRsaKey() {
        KeyPair keyPair = KeyGeneratorUtils.generateRsaKey();
        assertNotNull(keyPair);
        assertNotNull(keyPair.getPublic());
        assertNotNull(keyPair.getPrivate());
        assertEquals("RSA", keyPair.getPublic().getAlgorithm());
    }

    @Test
    @DisplayName("Should generate recovery code in format XXX-XXX-XXX")
    void shouldGenerateRecoveryCodeInCorrectFormat() {
        String recoveryCode = KeyGeneratorUtils.generateRecoveryCodeToResetPassword();
        assertNotNull(recoveryCode);
        assertTrue(recoveryCode.matches("\\d{3}-\\d{3}-\\d{3}"), "Recovery code should match pattern XXX-XXX-XXX but was: " + recoveryCode);
    }

    @Test
    @DisplayName("Should generate different recovery codes on multiple calls")
    void shouldGenerateDifferentCodesOnMultipleCalls() {
        String code1 = KeyGeneratorUtils.generateRecoveryCodeToResetPassword();
        String code2 = KeyGeneratorUtils.generateRecoveryCodeToResetPassword();
        assertNotEquals(code1, code2);
    }
}
