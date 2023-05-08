package br.com.marcoshssilva.mhpasswordmanager.passwordservice.web.controllers;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt.CryptService;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.UserRegistrationNotFoundException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.UserRegistrationService;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.models.UserRegisteredModel;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.web.data.requests.CryptKeyRequest;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.web.data.responses.DecryptKeyBase64Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;

@RestController
@RequestMapping("/crypt")
@Slf4j
@Tag(name = "Encrypt/Decrypt Operations")
@SecurityRequirement(name = "OAuth2 Authorization Code Flow")
@SecurityRequirement(name = "Bearer Authorization")
public class CryptKeyController {
    public static final Base64.Encoder encoder = Base64.getEncoder();
    public static final Base64.Decoder decoder = Base64.getDecoder();

    private final CryptService cryptAesService;
    private final CryptService cryptRsaService;
    private final UserRegistrationService userRegistrationService;
    private final ObjectMapper mapper;


    public CryptKeyController(@Qualifier("aesCryptService") CryptService cryptAesService, @Qualifier("rsaCryptService") CryptService cryptRsaService, UserRegistrationService userRegistrationService, ObjectMapper mapper) {
        this.cryptAesService = cryptAesService;
        this.cryptRsaService = cryptRsaService;
        this.userRegistrationService = userRegistrationService;
        this.mapper = mapper;
    }

    @PostMapping("/rsa/decrypt/base64")
    public ResponseEntity<DecryptKeyBase64Response> decryptRsaDataAsBase64(@AuthenticationPrincipal Jwt token, @RequestBody CryptKeyRequest payload) throws UserRegistrationNotFoundException {
        byte[] decryptedRsaData = decryptRsaData(decoder.decode(payload.getBase64Data()), payload.getSecret(), token.getSubject());
        return ResponseEntity.ok(
                DecryptKeyBase64Response.builder()
                        .data(encoder.encodeToString(decryptedRsaData))
                        .build()
        );
    }

    @PostMapping("/rsa/decrypt/json")
    public ResponseEntity<JsonNode> decryptRsaDataAsJson(@AuthenticationPrincipal Jwt token, @RequestBody CryptKeyRequest payload) throws UserRegistrationNotFoundException, JsonProcessingException {
        byte[] decryptedRsaData = decryptRsaData(decoder.decode(payload.getBase64Data()), payload.getSecret(), token.getSubject());
        JsonNode json = mapper.readTree(cryptRsaService.convertByteToString(decryptedRsaData));
        return ResponseEntity.ok(json);
    }

    @PostMapping("/aes/decrypt/base64")
    public ResponseEntity<DecryptKeyBase64Response> decryptAesDataAsBase64(@RequestBody CryptKeyRequest payload) {
        byte[] decrypted = decryptAesData(decoder.decode(payload.getBase64Data()), payload.getSecret());
        return ResponseEntity.ok(
                DecryptKeyBase64Response.builder()
                        .data(encoder.encodeToString(decrypted))
                        .build()
        );
    }

    @PostMapping("/aes/decrypt/json")
    public ResponseEntity<JsonNode> decryptAesDataAsJson(@RequestBody CryptKeyRequest payload) throws JsonProcessingException {
        byte[] decrypted = decryptAesData(decoder.decode(payload.getBase64Data()), payload.getSecret());
        JsonNode json = mapper.readTree(cryptRsaService.convertByteToString(decrypted));
        return ResponseEntity.ok(json);
    }

    @PostMapping("/aes/encrypt/base64")
    public ResponseEntity<String> encryptDataInAesAndConvertAsBase64(@RequestBody CryptKeyRequest payload) {
        byte[] decoded = decoder.decode(payload.getBase64Data());
        byte[] encrypted = cryptAesService.encrypt(decoded, payload.getSecret());

        return ResponseEntity.ok(cryptAesService.convertByteToBase64(encrypted));
    }

    @PostMapping("/rsa/encrypt/base64")
    public ResponseEntity<String> encryptDataInRsaAndConvertAsBase64(@AuthenticationPrincipal Jwt token, @RequestBody CryptKeyRequest payload) throws UserRegistrationNotFoundException {
        UserRegisteredModel userRegistration = userRegistrationService.getUserRegistration(token.getSubject());

        String publicKey = userRegistration.getPublicKey();
        byte[] encrypted = cryptRsaService.encrypt(decoder.decode(payload.getBase64Data()), publicKey);
        String encodeToString = encoder.encodeToString(encrypted);

        return ResponseEntity.ok(encodeToString);
    }

    private byte[] decryptRsaData(byte[] data, String secret, String userRegistrationEmail) throws UserRegistrationNotFoundException {

        UserRegisteredModel userRegistration = userRegistrationService.getUserRegistration(userRegistrationEmail);
        String key = "encrypted-default";
        String privKeyBase64 = userRegistration.getKeys().get(key);

        byte[] decodedPrivateKey = decoder.decode(privKeyBase64.getBytes());
        byte[] decryptedPrivateKeyEncoded = cryptAesService.decrypt(decodedPrivateKey, secret);

        return cryptRsaService.decrypt(
                data,
                cryptRsaService.convertByteToBase64(decryptedPrivateKeyEncoded));
    }

    private byte[] decryptAesData(byte[] data, String secret) {
        return cryptAesService.decrypt(data, secret);
    }

}
