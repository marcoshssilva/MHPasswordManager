package br.com.marcoshssilva.mhpasswordmanager.passwordservice.web.controllers;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt.CryptService;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt.exceptions.DecryptionException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.UserBucketService;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.exceptions.BucketNotFoundException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.models.BucketDataModel;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.common.IResultData;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.UserAuthorizations;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.exceptions.UserAuthorizationCannotBeLoadedException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.UserRegistrationService;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.web.data.requests.AesCryptKeyRequest;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.web.data.requests.RsaCryptKeyRequest;
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
    private final UserBucketService userBucketService;


    public CryptKeyController(@Qualifier("aesCryptService") CryptService cryptAesService, @Qualifier("rsaCryptService") CryptService cryptRsaService, UserRegistrationService userRegistrationService, ObjectMapper mapper, UserBucketService userBucketService) {
        this.cryptAesService = cryptAesService;
        this.cryptRsaService = cryptRsaService;
        this.userRegistrationService = userRegistrationService;
        this.mapper = mapper;
        this.userBucketService = userBucketService;
    }

    @PostMapping("/rsa/decrypt/base64")
    public ResponseEntity<DecryptKeyBase64Response> decryptRsaDataAsBase64(@AuthenticationPrincipal Jwt token, @RequestBody RsaCryptKeyRequest payload) throws UserAuthorizationCannotBeLoadedException {
        final UserAuthorizations authorizations = userRegistrationService.getUserAuthorizations(token);
        byte[] decryptedRsaData = decryptRsaData(decoder.decode(payload.getBase64Data()), payload.getSecret(), authorizations, payload.getBucketUuid());
        return ResponseEntity.ok(
                DecryptKeyBase64Response.builder()
                        .data(encoder.encodeToString(decryptedRsaData))
                        .build()
        );
    }

    @PostMapping("/rsa/decrypt/json")
    public ResponseEntity<JsonNode> decryptRsaDataAsJson(@AuthenticationPrincipal Jwt token, @RequestBody RsaCryptKeyRequest payload) throws JsonProcessingException, UserAuthorizationCannotBeLoadedException {
        final UserAuthorizations authorizations = userRegistrationService.getUserAuthorizations(token);
        byte[] decryptedRsaData = decryptRsaData(decoder.decode(payload.getBase64Data()), payload.getSecret(), authorizations, payload.getBucketUuid());
        JsonNode json = mapper.readTree(cryptRsaService.convertByteToString(decryptedRsaData));
        return ResponseEntity.ok(json);
    }

    @PostMapping("/aes/decrypt/base64")
    public ResponseEntity<DecryptKeyBase64Response> decryptAesDataAsBase64(@RequestBody AesCryptKeyRequest payload) {
        byte[] decrypted = decryptAesData(decoder.decode(payload.getBase64Data()), payload.getSecret());
        return ResponseEntity.ok(
                DecryptKeyBase64Response.builder()
                        .data(encoder.encodeToString(decrypted))
                        .build()
        );
    }

    @PostMapping("/aes/decrypt/json")
    public ResponseEntity<JsonNode> decryptAesDataAsJson(@RequestBody AesCryptKeyRequest payload) throws JsonProcessingException {
        byte[] decrypted = decryptAesData(decoder.decode(payload.getBase64Data()), payload.getSecret());
        JsonNode json = mapper.readTree(cryptRsaService.convertByteToString(decrypted));
        return ResponseEntity.ok(json);
    }

    @PostMapping("/aes/encrypt/base64")
    public ResponseEntity<String> encryptDataInAesAndConvertAsBase64(@RequestBody AesCryptKeyRequest payload) {
        byte[] decoded = decoder.decode(payload.getBase64Data());
        byte[] encrypted = cryptAesService.encrypt(decoded, payload.getSecret());

        return ResponseEntity.ok(cryptAesService.convertByteToBase64(encrypted));
    }

    @PostMapping("/rsa/encrypt/base64")
    public ResponseEntity<String> encryptDataInRsaAndConvertAsBase64(@AuthenticationPrincipal Jwt token, @RequestBody RsaCryptKeyRequest payload) throws UserAuthorizationCannotBeLoadedException {
        final UserAuthorizations authorizations = userRegistrationService.getUserAuthorizations(token);
        final IResultData<BucketDataModel> resultBucket = userBucketService.getBucketByUuid(payload.getBucketUuid(), authorizations);
        if (Boolean.TRUE.equals(resultBucket.hasError())) {
            throw new DecryptionException("Cannot fetch Bucket data. MESSAGE: " + resultBucket.getMessage(), Boolean.TRUE.equals(resultBucket.hasException()) ? resultBucket.getException() : new BucketNotFoundException());
        }

        String publicKey = resultBucket.getData().getBucketPublicKey();
        byte[] encrypted = cryptRsaService.encrypt(decoder.decode(payload.getBase64Data()), publicKey);
        String encodeToString = encoder.encodeToString(encrypted);

        return ResponseEntity.ok(encodeToString);
    }

    private byte[] decryptRsaData(byte[] data, String secret, UserAuthorizations userAuthorization, String bucketUuid) {

        final IResultData<BucketDataModel> resultBucket = userBucketService.getBucketByUuid(bucketUuid, userAuthorization);
        if (Boolean.TRUE.equals(resultBucket.hasError())) {
            throw new DecryptionException("Cannot fetch Bucket data. MESSAGE: " + resultBucket.getMessage(), Boolean.TRUE.equals(resultBucket.hasException()) ? resultBucket.getException() : new BucketNotFoundException());
        }
        String privateKeyBase64 = resultBucket.getData().getBucketPrivateKeyEncrypted();

        byte[] decodedPrivateKey = decoder.decode(privateKeyBase64.getBytes());
        byte[] decryptedPrivateKeyEncoded = cryptAesService.decrypt(decodedPrivateKey, secret);

        return cryptRsaService.decrypt(
                data,
                cryptRsaService.convertByteToBase64(decryptedPrivateKeyEncoded));
    }

    private byte[] decryptAesData(byte[] data, String secret) {
        return cryptAesService.decrypt(data, secret);
    }

}
