package br.com.marcoshssilva.mhpasswordmanager.configservices.crypto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.rsa.crypto.RsaSecretEncryptor;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPairGenerator;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "spring.profiles.active=local,native",
        "config.configserver.crypto.enabled=true",
        "config.configserver.crypto.password=test-password",
        "eureka.client.enabled=false",
        "spring.cloud.config.enabled=false",
        "otel.sdk.disabled=true"
})
class ConfigEncryptionIntegrationTests {
    @TempDir
    static Path directory;

    @Autowired
    TestRestTemplate rest;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("config.configserver.crypto.location", () -> directory.resolve("config.p12").toString());
        registry.add("spring.cloud.config.server.native.search-locations", () -> directory.toUri().toString());
    }

    @Test
    @DisplayName("Should serve a configuration property decrypted after encrypting it")
    void encryptsAndServesDecryptedProperty() throws Exception {
        String cipher = postText("/encrypt", "example-secret").getBody();
        assertThat(cipher).isNotBlank().doesNotContain("example-secret");
        Files.writeString(directory.resolve("example.yml"), "my-secret: '{cipher}" + cipher + "'\n");
        String response = rest.getForObject("/example/default", String.class);
        assertThat(response).contains("example-secret").doesNotContain(cipher);
        assertThat(Files.exists(directory.resolve("config.p12"))).isTrue();
    }

    @Test
    @DisplayName("Should preserve UTF-8 characters and whitespace when encrypting and decrypting")
    void encryptAndDecryptPreserveUtf8AndWhitespace() {
        String secret = "  senha: café + = !\nsegunda linha  ";
        ResponseEntity<String> encrypted = postText("/encrypt", secret);

        assertThat(encrypted.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(encrypted.getBody()).isNotBlank().doesNotContain(secret);
        ResponseEntity<String> decrypted = postText("/decrypt", encrypted.getBody());
        assertThat(decrypted.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(decrypted.getBody()).isEqualTo(secret);
    }

    @Test
    @DisplayName("Should use the same key for named and default encryption endpoints")
    void namedAndDefaultEndpointsUseTheSameKey() {
        ResponseEntity<String> encrypted = postText("/encrypt/example/docker0", "named-secret");
        assertThat(encrypted.getStatusCode()).isEqualTo(HttpStatus.OK);
        ResponseEntity<String> decrypted = postText("/decrypt", encrypted.getBody());
        assertThat(decrypted.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(decrypted.getBody()).isEqualTo("named-secret");

        encrypted = postText("/encrypt", "default-secret");
        assertThat(encrypted.getStatusCode()).isEqualTo(HttpStatus.OK);
        decrypted = postText("/decrypt/example/docker0", encrypted.getBody());
        assertThat(decrypted.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(decrypted.getBody()).isEqualTo("default-secret");
    }

    @Test
    @DisplayName("Should produce different ciphertexts when encrypting the same value twice")
    void encryptingTheSameValueTwiceProducesDifferentCiphertexts() {
        ResponseEntity<String> first = postText("/encrypt", "repeated-secret");
        ResponseEntity<String> second = postText("/encrypt", "repeated-secret");
        assertThat(first.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(second.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(first.getBody()).isNotBlank().isNotEqualTo(second.getBody());
        assertThat(postText("/decrypt", first.getBody()).getBody()).isEqualTo("repeated-secret");
        assertThat(postText("/decrypt", second.getBody()).getBody()).isEqualTo("repeated-secret");
    }

    @Test
    @DisplayName("Should reject malformed ciphertext")
    void decryptRejectsMalformedCiphertext() {
        ResponseEntity<String> response = postText("/decrypt", "not-base64!");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("INVALID");
    }

    @Test
    @DisplayName("Should reject ciphertext encrypted with another key")
    void decryptRejectsCiphertextFromAnotherKey() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        String cipher = new RsaSecretEncryptor(generator.generateKeyPair()).encrypt("foreign-secret");
        ResponseEntity<String> response = postText("/decrypt", cipher);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("INVALID").doesNotContain("foreign-secret");
    }

    private ResponseEntity<String> postText(String path, String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("text", "plain", StandardCharsets.UTF_8));
        return rest.postForEntity(path, new HttpEntity<>(body, headers), String.class);
    }
}
