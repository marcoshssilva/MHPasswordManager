package br.com.marcoshssilva.mhpasswordmanager.passwordservice.web.controllers;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.UserBucketService;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.models.BucketDataModel;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.common.IResultData;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.UserKeysService;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.UserStoredKeysService;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.*;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.UserAuthorizations;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.UserRegistrationService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.Arrays;

@RestController
@RequestMapping("/keys")
@Tag(name = "Keys Operations")
@SecurityRequirement(name = "OAuth2 Authorization Code Flow")
@SecurityRequirement(name = "Bearer Authorization")

@lombok.RequiredArgsConstructor
public class ManageKeysController {
    private final UserRegistrationService userRegistrationService;
    private final UserKeysService userKeysService;
    private final UserStoredKeysService userStoredKeysService;
    private final UserBucketService userBucketService;

    @GetMapping("{bucketUuid}/key/all")
    @Transactional(rollbackOn = Exception.class)
    public ResponseEntity<Page<KeyPayloadEncodedDto>> getAllKeys(@AuthenticationPrincipal Jwt token, @PathVariable("bucketUuid") String uuid, @ParameterObject @PageableDefault(size = 500) Pageable pageable) throws Exception {
        final UserAuthorizations authorizations = userRegistrationService.getUserAuthorizations(token);
        final IResultData<Page<KeyPayloadEncodedDto>> resultData = userKeysService.getAllEncodedKeyFromBucket(authorizations, uuid, pageable);
        resultData.throwErrorIfExists();
        return ResponseEntity.ok(resultData.getData());
    }

    @GetMapping("{bucketUuid}/key/{id}")
    @Transactional(rollbackOn = Exception.class)
    public ResponseEntity<KeyPayloadEncodedDto> getKey(@AuthenticationPrincipal Jwt token, @PathVariable("bucketUuid") String uuid, @PathVariable("id") Long id) throws Exception {
        final UserAuthorizations authorizations = userRegistrationService.getUserAuthorizations(token);
        final IResultData<KeyPayloadEncodedDto> resultData = userKeysService.getEncodedKeyFromBucket(authorizations, uuid, id);
        resultData.throwErrorIfExists();
        return ResponseEntity.ok(resultData.getData());
    }

    @PostMapping("/{bucketUuid}/key/new")
    @Transactional(rollbackOn = Exception.class)
    public ResponseEntity<KeyPayloadEncodedDto> saveKey(@AuthenticationPrincipal Jwt token, @RequestBody AbstractKeyPayloadDecodedDto payload, @PathVariable("bucketUuid") String uuid) throws Exception {
        final UserAuthorizations authorizations = userRegistrationService.getUserAuthorizations(token);
        final IResultData<BucketDataModel> bucket = userBucketService.getBucketByUuid(uuid, authorizations);
        bucket.throwErrorIfExists();

        payload.setKeyId(null);
        payload.setOwnerId(uuid);
        final IResultData<KeyPayloadEncodedDto> keyPayloadEncodedDtoIResultData = userKeysService.transformAsKeyPayloadEncodedDto(payload, bucket.getData().getBucketPublicKey());
        keyPayloadEncodedDtoIResultData.throwErrorIfExists();

        Arrays.stream(keyPayloadEncodedDtoIResultData.getData().getEncodedKeys()).forEach(item -> item.setId(null));
        final IResultData<KeyPayloadEncodedDto> savedResultData = userKeysService.saveKeyPayloadEncodedDto(authorizations, keyPayloadEncodedDtoIResultData.getData());
        savedResultData.throwErrorIfExists();

        return ResponseEntity.ok(savedResultData.getData());
    }

    @PutMapping("/{bucketUuid}/key/{id}")
    @Transactional(rollbackOn = Exception.class)
    public ResponseEntity<KeyPayloadEncodedDto> updateKey(@AuthenticationPrincipal Jwt token, @PathVariable("bucketUuid") String uuid, @PathVariable("id") Long id, @RequestBody AbstractKeyPayloadDecodedDto payload) throws Exception {
        final UserAuthorizations authorizations = userRegistrationService.getUserAuthorizations(token);
        final IResultData<BucketDataModel> bucket = userBucketService.getBucketByUuid(uuid, authorizations);
        bucket.throwErrorIfExists();

        final IResultData<KeyPayloadEncodedDto> keyPayloadEncodedDtoIResultData = userKeysService.transformAsKeyPayloadEncodedDto(payload, bucket.getData().getBucketPublicKey());
        keyPayloadEncodedDtoIResultData.throwErrorIfExists();

        keyPayloadEncodedDtoIResultData.getData().setId(id);
        keyPayloadEncodedDtoIResultData.getData().setOwnerId(uuid);

        final IResultData<KeyPayloadEncodedDto> savedResultData = userKeysService.saveKeyPayloadEncodedDto(authorizations, keyPayloadEncodedDtoIResultData.getData());
        savedResultData.throwErrorIfExists();

        return ResponseEntity.ok(savedResultData.getData());
    }

    @DeleteMapping("/{bucketUuid}/key/{id}")
    @Transactional(rollbackOn = Exception.class)
    public ResponseEntity<Void> deleteKey(@AuthenticationPrincipal Jwt token, @PathVariable("bucketUuid") String uuid, @PathVariable("id") Long id) throws Exception {
        final UserAuthorizations authorizations = userRegistrationService.getUserAuthorizations(token);
        final IResultData<Void> resultData = userKeysService.deleteKeyPayload(authorizations, uuid, id);
        resultData.throwErrorIfExists();
        return ResponseEntity.ok().build();
    }

    @GetMapping("{bucketUuid}/key/{id}/stored/{keyStoreId}")
    @Transactional(rollbackOn = Exception.class)
    public ResponseEntity<KeyStorePayloadEncodedDto> getKeyPayload(@AuthenticationPrincipal Jwt token, @PathVariable("bucketUuid") String uuid, @PathVariable("id") Long id, @PathVariable("keyStoreId") Long keyStoredId) throws Exception {
        final UserAuthorizations authorizations = userRegistrationService.getUserAuthorizations(token);
        final IResultData<KeyStorePayloadEncodedDto> storedKey = userStoredKeysService.getStoredKey(authorizations, uuid, id, keyStoredId);
        storedKey.throwErrorIfExists();
        return ResponseEntity.ok(storedKey.getData());
    }

    @PostMapping("{bucketUuid}/key/{id}/stored/new/password")
    @Transactional(rollbackOn = Exception.class)
    public ResponseEntity<KeyStorePayloadEncodedDto> saveKeyPayload(@AuthenticationPrincipal Jwt token, @PathVariable("bucketUuid") String uuid, @PathVariable("id") Long id, @RequestBody AbstractPasswordStoredValueDecodedDto body) throws Exception {
        final UserAuthorizations authorizations = userRegistrationService.getUserAuthorizations(token);
        final IResultData<KeyStorePayloadEncodedDto> passwordStoredKey = userStoredKeysService.createPasswordStoredKey(authorizations, uuid, id, body);
        passwordStoredKey.throwErrorIfExists();
        return ResponseEntity.ok(passwordStoredKey.getData());
    }

    @PutMapping("{bucketUuid}/key/{keyId}/stored/{keyStoredId}/password")
    @Transactional(rollbackOn = Exception.class)
    public ResponseEntity<KeyStorePayloadEncodedDto> updateKeyPayload(@AuthenticationPrincipal Jwt token, @PathVariable("bucketUuid") String uuid, @PathVariable("keyId") Long keyId, @PathVariable("keyStoredId") Long keyStoredId, @RequestBody AbstractPasswordStoredValueDecodedDto body) throws Exception {
        final UserAuthorizations authorizations = userRegistrationService.getUserAuthorizations(token);
        final IResultData<KeyStorePayloadEncodedDto> keyStorePayloadEncodedDtoIResultData = userStoredKeysService.savePasswordStoredKey(authorizations, uuid, keyId, keyStoredId, body);
        keyStorePayloadEncodedDtoIResultData.throwErrorIfExists();
        return ResponseEntity.ok(keyStorePayloadEncodedDtoIResultData.getData());
    }

    @PostMapping("{bucketUuid}/key/{id}/stored/new/security-question")
    @Transactional(rollbackOn = Exception.class)
    public ResponseEntity<KeyStorePayloadEncodedDto> saveKeyPayloadSecurity(@AuthenticationPrincipal Jwt token, @PathVariable("bucketUuid") String uuid, @PathVariable("id") Long id, @RequestBody AbstractSecurityQuestionStoredValueDecodedDto body) throws Exception {
        final UserAuthorizations authorizations = userRegistrationService.getUserAuthorizations(token);
        final IResultData<KeyStorePayloadEncodedDto> securityQuestionStoredKey = userStoredKeysService.createSecurityQuestionStoredKey(authorizations, uuid, id, body);
        securityQuestionStoredKey.throwErrorIfExists();
        return ResponseEntity.ok(securityQuestionStoredKey.getData());
    }

    @PutMapping("{bucketUuid}/key/{keyId}/stored/{keyStoredId}/security-question")
    @Transactional(rollbackOn = Exception.class)
    public ResponseEntity<KeyStorePayloadEncodedDto> updateKeyPayloadSecurity(@AuthenticationPrincipal Jwt token, @PathVariable("bucketUuid") String uuid, @PathVariable("keyId") Long keyId, @PathVariable("keyStoredId") Long keyStoredId, @RequestBody AbstractSecurityQuestionStoredValueDecodedDto body) throws Exception {
        final UserAuthorizations authorizations = userRegistrationService.getUserAuthorizations(token);
        final IResultData<KeyStorePayloadEncodedDto> keyStorePayloadEncodedDtoIResultData = userStoredKeysService.saveStoredQuestionStoredKey(authorizations, uuid, keyId, keyStoredId, body);
        keyStorePayloadEncodedDtoIResultData.throwErrorIfExists();
        return ResponseEntity.ok(keyStorePayloadEncodedDtoIResultData.getData());
    }

    @DeleteMapping("{bucketUuid}/key/{keyId}/stored/{keyStoredId}")
    @Transactional(rollbackOn = Exception.class)
    public ResponseEntity<Void> deleteKeyPayload(@AuthenticationPrincipal Jwt token, @PathVariable("bucketUuid") String uuid, @PathVariable("keyId") Long keyId, @PathVariable("keyStoredId") Long keyStoredId) throws Exception {
        final UserAuthorizations authorizations = userRegistrationService.getUserAuthorizations(token);
        final IResultData<Void> resultData = userStoredKeysService.deleteStoredKey(authorizations, uuid, keyId, keyStoredId);
        resultData.throwErrorIfExists();
        return ResponseEntity.ok().build();
    }
}
