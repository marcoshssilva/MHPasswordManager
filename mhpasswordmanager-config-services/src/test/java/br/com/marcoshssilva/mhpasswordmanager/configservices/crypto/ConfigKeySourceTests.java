package br.com.marcoshssilva.mhpasswordmanager.configservices.crypto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.core.env.SystemEnvironmentPropertySource;
import org.springframework.security.rsa.crypto.RsaSecretEncryptor;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyStore;
import java.util.Base64;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConfigKeySourceTests {
    @TempDir
    Path directory;

    @Test
    @DisplayName("Should generate a local keystore once and reload its key")
    void generatesOnceAndReloadsKeyInLocalProfile() throws Exception {
        MockEnvironment env = keystoreEnvironment("local");
        KeyPair first = ConfigKeySource.load(env);
        KeyPair second = ConfigKeySource.load(env);
        assertThat(second.getPublic().getEncoded()).isEqualTo(first.getPublic().getEncoded());
        String cipher = new RsaSecretEncryptor(first).encrypt("secret");
        assertThat(new RsaSecretEncryptor(second).decrypt(cipher)).isEqualTo("secret");
    }

    @Test
    @DisplayName("Should fail outside the local profile and preserve the keystore on incorrect password")
    void missingStoreFailsOutsideLocalAndWrongPasswordDoesNotReplaceIt() throws Exception {
        MockEnvironment env = keystoreEnvironment("production");
        assertThatThrownBy(() -> ConfigKeySource.load(env)).isInstanceOf(CryptoErrorException.class);
        env.setActiveProfiles("local");
        ConfigKeySource.load(env);
        byte[] before = Files.readAllBytes(directory.resolve("keys.p12"));
        env.setProperty("config.configserver.crypto.password", "incorrect");
        assertThatThrownBy(() -> ConfigKeySource.load(env)).isInstanceOf(CryptoErrorException.class);
        assertThat(Files.readAllBytes(directory.resolve("keys.p12"))).isEqualTo(before);
    }

    @Test
    @DisplayName("Should read environment variables and honor disabled local generation")
    void supportsEnvironmentVariablesAndCanDisableLocalGeneration() {
        MockEnvironment env = new MockEnvironment();
        env.setActiveProfiles("local");
        env.getPropertySources().addFirst(new SystemEnvironmentPropertySource("systemEnvironment", Map.of(
                "CONFIG_CONFIGSERVER_CRYPTO_LOCATION", directory.resolve("keys.p12").toString(),
                "CONFIG_CONFIGSERVER_CRYPTO_PASSWORD", "test-password",
                "CONFIG_CONFIGSERVER_CRYPTO_GENERATE", "false")));
        assertThatThrownBy(() -> ConfigKeySource.load(env))
                .isInstanceOf(CryptoErrorException.class).hasMessageContaining("does not exist");
        assertThat(Files.exists(directory.resolve("keys.p12"))).isFalse();
    }

    @Test
    @DisplayName("Should load PEM key material and reject mismatched certificates")
    void loadsPemAndRejectsMismatchedCertificate() throws Exception {
        MockEnvironment env = keystoreEnvironment("local");
        KeyPair pair = ConfigKeySource.load(env);
        KeyStore store = KeyStore.getInstance("PKCS12");
        try (InputStream input = Files.newInputStream(directory.resolve("keys.p12"))) {
            store.load(input, "test-password".toCharArray());
        }
        Path certificate = directory.resolve("certificate.pem");
        Path key = directory.resolve("private.pem");
        Files.writeString(certificate, pem("CERTIFICATE", store.getCertificate("config-server").getEncoded()));
        Files.writeString(key, pem("PRIVATE KEY", pair.getPrivate().getEncoded()));
        env.setProperty("config.configserver.crypto.source", "pem");
        env.setProperty("config.configserver.crypto.certificate", certificate.toString());
        env.setProperty("config.configserver.crypto.private-key", key.toString());
        String cipher = new RsaSecretEncryptor(pair).encrypt("secret");
        assertThat(new RsaSecretEncryptor(ConfigKeySource.load(env)).decrypt(cipher)).isEqualTo("secret");

        java.security.KeyPairGenerator generator = java.security.KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        Files.writeString(key, pem("PRIVATE KEY", generator.generateKeyPair().getPrivate().getEncoded()));
        assertThatThrownBy(() -> ConfigKeySource.load(env))
                .isInstanceOf(CryptoErrorException.class).hasMessageContaining("do not match");
    }

    @Test
    @DisplayName("Should throw CryptoErrorException when unsupported source is provided")
    void throwsCryptoErrorExceptionForUnsupportedSource() {
        MockEnvironment env = keystoreEnvironment("local");
        env.setProperty("config.configserver.crypto.source", "unsupported-source");
        assertThatThrownBy(() -> ConfigKeySource.load(env))
                .isInstanceOf(CryptoErrorException.class)
                .hasMessageContaining("Unsupported config encryption key source");
    }

    @Test
    @DisplayName("Should throw CryptoErrorException when missing required password property")
    void throwsCryptoErrorExceptionForMissingPassword() {
        MockEnvironment env = new MockEnvironment();
        env.setActiveProfiles("local");
        env.setProperty("config.configserver.crypto.location", directory.resolve("keys.p12").toString());
        assertThatThrownBy(() -> ConfigKeySource.load(env))
                .isInstanceOf(CryptoErrorException.class)
                .hasMessageContaining("Missing required property");
    }

    @Test
    @DisplayName("Should throw CryptoErrorException with cause for PEM certificate file not found")
    void throwsCryptoErrorExceptionWithCauseForMissingPemFile() {
        MockEnvironment env = new MockEnvironment();
        env.setActiveProfiles("local");
        env.setProperty("config.configserver.crypto.source", "pem");
        env.setProperty("config.configserver.crypto.certificate", directory.resolve("nonexistent.pem").toString());
        env.setProperty("config.configserver.crypto.private-key", directory.resolve("key.pem").toString());
        assertThatThrownBy(() -> ConfigKeySource.load(env))
                .isInstanceOf(CryptoErrorException.class)
                .hasMessageContaining("Failed to load PEM files")
                .hasCauseInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("Should throw CryptoErrorException for invalid PEM format")
    void throwsCryptoErrorExceptionForInvalidPemFormat() throws Exception {
        MockEnvironment env = keystoreEnvironment("local");
        Path certificate = directory.resolve("certificate.pem");
        Path key = directory.resolve("private.pem");
        
        // Generate initial keystore first
        ConfigKeySource.load(env);
        
        KeyStore store = KeyStore.getInstance("PKCS12");
        try (InputStream input = Files.newInputStream(directory.resolve("keys.p12"))) {
            store.load(input, "test-password".toCharArray());
        }
        Files.writeString(certificate, pem("CERTIFICATE", store.getCertificate("config-server").getEncoded()));
        Files.writeString(key, "INVALID PEM FORMAT");
        
        env.setProperty("config.configserver.crypto.source", "pem");
        env.setProperty("config.configserver.crypto.certificate", certificate.toString());
        env.setProperty("config.configserver.crypto.private-key", key.toString());
        
        assertThatThrownBy(() -> ConfigKeySource.load(env))
                .isInstanceOf(CryptoErrorException.class)
                .hasMessageContaining("PEM private key must be unencrypted PKCS#8");
    }

    @Test
    @DisplayName("Should preserve cause in CryptoErrorException when IOException occurs")
    void preservesCauseInCryptoErrorException() {
        MockEnvironment env = keystoreEnvironment("production");
        assertThatThrownBy(() -> ConfigKeySource.load(env))
                .isInstanceOf(CryptoErrorException.class)
                .satisfies(exception -> {
                    CryptoErrorException cryptoException = (CryptoErrorException) exception;
                    assertThat(cryptoException.getMessage()).contains("Config encryption keystore does not exist");
                });
    }

    @Test
    @DisplayName("Should wrap IOException with CryptoErrorException message")
    void wrapsIOExceptionWithCryptoErrorException() throws Exception {
        MockEnvironment env = keystoreEnvironment("local");
        Path keystore = directory.resolve("keys.p12");
        
        // Generate initial keystore
        ConfigKeySource.load(env);
        
        // Make keystore unreadable to simulate IOException
        Files.delete(keystore);
        env.setProperty("config.configserver.crypto.generate", "false");
        
        assertThatThrownBy(() -> ConfigKeySource.load(env))
                .isInstanceOf(CryptoErrorException.class)
                .hasMessageContaining("does not exist");
    }

    @Test
    @DisplayName("Should provide cause for root cause analysis")
    void cryptoErrorExceptionPreservesStackTrace() {
        MockEnvironment env = keystoreEnvironment("local");
        env.setProperty("config.configserver.crypto.source", "pem");
        env.setProperty("config.configserver.crypto.certificate", "/nonexistent/cert.pem");
        env.setProperty("config.configserver.crypto.private-key", "/nonexistent/key.pem");
        
        assertThatThrownBy(() -> ConfigKeySource.load(env))
                .isInstanceOf(CryptoErrorException.class)
                .satisfies(exception -> {
                    CryptoErrorException cryptoException = (CryptoErrorException) exception;
                    assertThat(cryptoException.getCause()).isNotNull();
                    assertThat(cryptoException.getCause().getMessage()).isNotEmpty();
                });
    }

    private MockEnvironment keystoreEnvironment(String profile) {
        MockEnvironment env = new MockEnvironment();
        env.setActiveProfiles(profile);
        env.setProperty("config.configserver.crypto.location", directory.resolve("keys.p12").toString());
        env.setProperty("config.configserver.crypto.password", "test-password");
        return env;
    }

    private String pem(String type, byte[] bytes) {
        return "-----BEGIN " + type + "-----\n"
                + Base64.getMimeEncoder(64, new byte[]{'\n'}).encodeToString(bytes)
                + "\n-----END " + type + "-----\n";
    }
}
