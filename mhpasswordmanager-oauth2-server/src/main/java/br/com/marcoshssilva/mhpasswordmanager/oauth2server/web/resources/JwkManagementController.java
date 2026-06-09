package br.com.marcoshssilva.mhpasswordmanager.oauth2server.web.resources;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.JwkKeyData;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.JwkKeyService;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.utils.JwksUtils;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.web.data.responses.HttpJsonResponse;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.RSAKey;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/jwk-management")
@RequiredArgsConstructor
@Tag(name = "JWK Management")
public class JwkManagementController {
    private final JwkKeyService jwkKeyService;

    @RequestMapping(value = "/get/all", method = RequestMethod.GET)
    public ResponseEntity<HttpJsonResponse<Collection<JwkKeyData>>> getAllJwkKeys() {
        return ResponseEntity.ok(HttpJsonResponse.<Collection<JwkKeyData>>builder().data(jwkKeyService.getAllKeys()).build());
    }

    @RequestMapping(value = "/get/{uuid}", method = RequestMethod.GET)
    public ResponseEntity<HttpJsonResponse<JwkKeyData>> getJwkKey(@PathVariable String uuid) {
        JwkKeyData jwkKey = jwkKeyService.getJwkKey(UUID.fromString(uuid));
        if (Objects.nonNull(jwkKey)) {
            return ResponseEntity.ok(HttpJsonResponse.<JwkKeyData>builder().data(jwkKey).build());
        }
        return ResponseEntity.notFound().build();
    }

    @RequestMapping(value = "/select/{uuid}", method = RequestMethod.GET)
    public ResponseEntity<HttpJsonResponse<Void>> selectJwkKey(@PathVariable String uuid) {
        jwkKeyService.selectJwkKey(UUID.fromString(uuid));
        return ResponseEntity.ok(HttpJsonResponse.<Void>builder().message("Jwk key selected with success").build());
    }

    @RequestMapping(value = "/delete/{uuid}", method = RequestMethod.DELETE)
    public ResponseEntity<HttpJsonResponse<Void>> deleteJwkKey(@PathVariable String uuid) {
        jwkKeyService.deleteJwkKey(UUID.fromString(uuid));
        return ResponseEntity.ok(HttpJsonResponse.<Void>builder().message("Jwk key deleted with success").build());
    }

    @RequestMapping(value = "/generate-self-signed", method = RequestMethod.POST)
    public ResponseEntity<HttpJsonResponse<JwkKeyData>> generateSelfSignedJwkKey() throws JOSEException {
        RSAKey rsaKey = JwksUtils.generateRsa();
        JwkKeyData jwkKey = jwkKeyService.createJwkKey(JwkKeyData.builder()
                .uuid(jwkKeyService.createNotUsedUUID().toString())
                .publicKey(new String(Base64.getEncoder().encode(rsaKey.toPublicKey().getEncoded()), StandardCharsets.UTF_8))
                .privateKey(new String(Base64.getEncoder().encode(rsaKey.toPrivateKey().getEncoded()), StandardCharsets.UTF_8))
                .algorithm("RSA")
                .active(Boolean.FALSE)
                .createdAt(LocalDateTime.now(Clock.systemUTC()))
                .build());

        return ResponseEntity.status(HttpStatus.CREATED).body(HttpJsonResponse.<JwkKeyData>builder()
                        .data(jwkKey)
                        .message("Jwk key created with success")
                        .build()
                );
    }

    @RequestMapping(value = "/import/rsa", method = RequestMethod.POST, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<HttpJsonResponse<JwkKeyData>> createRsaJwkKey(
            @RequestPart("private_key_file") MultipartFile privateKeyFile,
            @RequestPart("public_key_file")  MultipartFile publicKeyFile
    ) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED).body(HttpJsonResponse.<JwkKeyData>builder()
                        .data(jwkKeyService.createJwkKey(JwkKeyData.builder()
                            .privateKey(JwksUtils.getBase64PKCS8PrivateKeyFromBytes(privateKeyFile.getBytes(), "RSA"))
                            .publicKey(JwksUtils.getBase64X509PublicKeyFromBytes(publicKeyFile.getBytes(), "RSA"))
                            .algorithm("RSA")
                            .uuid(jwkKeyService.createNotUsedUUID().toString())
                            .active(Boolean.FALSE)
                            .createdAt(LocalDateTime.now(Clock.systemUTC()))
                            .build()))
                        .message("Jwk key created with success")
                        .build()
                );
    }
}
