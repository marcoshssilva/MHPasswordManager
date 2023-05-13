package br.com.marcoshssilva.mhpasswordmanager.passwordservice.web.controllers;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.KeyNotFoundException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.KeyRegistrationErrorException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.UserKeysService;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.UserStoredKeysService;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.converters.KeyEncodedErrorConverterException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.*;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.UserRegistrationNotFoundException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.UserRegistrationService;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.models.UserRegisteredModel;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/keys")
@RequiredArgsConstructor
@Tag(name = "Keys Operations")
@SecurityRequirement(name = "OAuth2 Authorization Code Flow")
@SecurityRequirement(name = "Bearer Authorization")
public class ManageKeysController {
    private final UserRegistrationService userRegistrationService;
    private final UserKeysService userKeysService;
    private final UserStoredKeysService userStoredKeysService;

    @GetMapping("{uuid}/key/all")
    public ResponseEntity<Page<KeyPayloadEncodedDto>> getAllKeys(@AuthenticationPrincipal Jwt token, @PathVariable("uuid") String uuid, @ParameterObject @PageableDefault(size = 500) Pageable pageable) throws UserRegistrationNotFoundException {

        UserRegisteredModel userRegistration = userRegistrationService.getUserRegistration(token.getSubject());
        if (Boolean.FALSE.equals(uuid.equals(userRegistration.getUuid()))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Page<KeyPayloadEncodedDto> allEncodedKey = userKeysService.getAllEncodedKey(token.getSubject(), pageable);
        return ResponseEntity.ok(allEncodedKey);
    }

    @GetMapping("{uuid}/key/{id}")
    public ResponseEntity<KeyPayloadEncodedDto> getKey(@AuthenticationPrincipal Jwt token, @PathVariable("uuid") String uuid, @PathVariable("id") Long id) throws UserRegistrationNotFoundException, KeyNotFoundException {

        UserRegisteredModel userRegistration = userRegistrationService.getUserRegistration(token.getSubject());
        if (Boolean.FALSE.equals(uuid.equals(userRegistration.getUuid()))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        KeyPayloadEncodedDto encodedKey = userKeysService.getEncodedKey(token.getSubject(), id);
        return ResponseEntity.ok(encodedKey);
    }

    @PostMapping("/{uuid}/key/new")
    public ResponseEntity<KeyPayloadEncodedDto> saveKey(@AuthenticationPrincipal Jwt token, @RequestBody AbstractKeyPayloadDecodedDto payload, @PathVariable("uuid") String uuid) throws UserRegistrationNotFoundException, KeyEncodedErrorConverterException, KeyRegistrationErrorException {

        UserRegisteredModel userRegistration = userRegistrationService.getUserRegistration(token.getSubject());
        if (Boolean.FALSE.equals(uuid.equals(userRegistration.getUuid()))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        payload.setOwnerId(uuid);
        KeyPayloadEncodedDto keyPayloadEncodedDto = userKeysService.transformAsKeyPayloadEncodedDto(payload, userRegistration.getPublicKey());
        keyPayloadEncodedDto.setId(null);
        Arrays.stream(keyPayloadEncodedDto.getEncodedKeys()).forEach(item -> item.setId(null));

        KeyPayloadEncodedDto saved = userKeysService.saveKeyPayloadEncodedDto(keyPayloadEncodedDto);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{uuid}/key/{id}")
    public ResponseEntity<KeyPayloadEncodedDto> updateKey(@AuthenticationPrincipal Jwt token, @PathVariable("uuid") String uuid, @PathVariable("id") Long id, @RequestBody AbstractKeyPayloadDecodedDto payload) throws KeyRegistrationErrorException, UserRegistrationNotFoundException, KeyEncodedErrorConverterException {

        UserRegisteredModel userRegistration = userRegistrationService.getUserRegistration(token.getSubject());
        if (Boolean.FALSE.equals(uuid.equals(userRegistration.getUuid()))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        KeyPayloadEncodedDto keyPayloadEncodedDto = userKeysService.transformAsKeyPayloadEncodedDto(payload, userRegistration.getPublicKey());

        keyPayloadEncodedDto.setId(id);
        keyPayloadEncodedDto.setOwnerId(uuid);

        KeyPayloadEncodedDto saved = userKeysService.updateKeyPayloadEncodedDto(keyPayloadEncodedDto);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{uuid}/key/{id}")
    public ResponseEntity<Void> deleteKey(@AuthenticationPrincipal Jwt token, @PathVariable("uuid") String uuid, @PathVariable("id") Long id) throws UserRegistrationNotFoundException, KeyNotFoundException {

        UserRegisteredModel userRegistration = userRegistrationService.getUserRegistration(token.getSubject());
        if (Boolean.FALSE.equals(uuid.equals(userRegistration.getUuid()))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        userKeysService.deleteKeyPayload(token.getSubject(), id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("{uuid}/key/{id}/stored/{keyStoreId}")
    public ResponseEntity<KeyStorePayloadEncodedDto> getKeyPayload(@AuthenticationPrincipal Jwt token, @PathVariable("uuid") String uuid, @PathVariable("id") Long id, @PathVariable("keyStoreId") Long keyStoredId) throws UserRegistrationNotFoundException, KeyRegistrationErrorException, KeyNotFoundException {

        UserRegisteredModel userRegistration = userRegistrationService.getUserRegistration(token.getSubject());
        if (Boolean.FALSE.equals(uuid.equals(userRegistration.getUuid()))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        KeyStorePayloadEncodedDto storedKey = userStoredKeysService.getStoredKey(token.getSubject(), id, keyStoredId);
        return ResponseEntity.ok(storedKey);
    }

    @PostMapping("{uuid}/key/{id}/stored/new/password")
    public ResponseEntity<KeyStorePayloadEncodedDto> saveKeyPayload(@AuthenticationPrincipal Jwt token, @PathVariable("uuid") String uuid, @PathVariable("id") Long id, @RequestBody AbstractPasswordStoredValueDecodedDto body) throws KeyRegistrationErrorException, UserRegistrationNotFoundException, JsonProcessingException, KeyNotFoundException {

        UserRegisteredModel userRegistration = userRegistrationService.getUserRegistration(token.getSubject());
        if (Boolean.FALSE.equals(uuid.equals(userRegistration.getUuid()))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        KeyStorePayloadEncodedDto encodedDto = userStoredKeysService.createPasswordStoredKey(token.getSubject(), id, body);
        return ResponseEntity.ok(encodedDto);
    }

    @PutMapping("{uuid}/key/{keyId}/stored/{keyStoredId}/password")
    public ResponseEntity<KeyStorePayloadEncodedDto> updateKeyPayload(@AuthenticationPrincipal Jwt token, @PathVariable("uuid") String uuid, @PathVariable("keyId") Long keyId, @PathVariable("keyStoredId") Long keyStoredId, @RequestBody AbstractPasswordStoredValueDecodedDto body) throws KeyRegistrationErrorException, UserRegistrationNotFoundException, KeyNotFoundException {

        UserRegisteredModel userRegistration = userRegistrationService.getUserRegistration(token.getSubject());
        if (Boolean.FALSE.equals(uuid.equals(userRegistration.getUuid()))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        KeyStorePayloadEncodedDto encodedDto = userStoredKeysService.savePasswordStoredKey(token.getSubject(), keyId, keyStoredId, body);
        return ResponseEntity.ok(encodedDto);
    }

    @PostMapping("{uuid}/key/{id}/stored/new/security-question")
    public ResponseEntity<KeyStorePayloadEncodedDto> saveKeyPayloadSecurity(@AuthenticationPrincipal Jwt token, @PathVariable("uuid") String uuid, @PathVariable("id") Long id, @RequestBody AbstractSecurityQuestionStoredValueDecodedDto body) throws KeyRegistrationErrorException, UserRegistrationNotFoundException, JsonProcessingException, KeyNotFoundException {

        UserRegisteredModel userRegistration = userRegistrationService.getUserRegistration(token.getSubject());
        if (Boolean.FALSE.equals(uuid.equals(userRegistration.getUuid()))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        KeyStorePayloadEncodedDto encodedDto = userStoredKeysService.createSecurityQuestionStoredKey(token.getSubject(), id, body);
        return ResponseEntity.ok(encodedDto);
    }

    @PutMapping("{uuid}/key/{keyId}/stored/{keyStoredId}/security-question")
    public ResponseEntity<KeyStorePayloadEncodedDto> updateKeyPayloadSecurity(@AuthenticationPrincipal Jwt token, @PathVariable("uuid") String uuid, @PathVariable("keyId") Long keyId, @PathVariable("keyStoredId") Long keyStoredId, @RequestBody AbstractSecurityQuestionStoredValueDecodedDto body) throws KeyRegistrationErrorException, UserRegistrationNotFoundException, KeyNotFoundException {

        UserRegisteredModel userRegistration = userRegistrationService.getUserRegistration(token.getSubject());
        if (Boolean.FALSE.equals(uuid.equals(userRegistration.getUuid()))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        KeyStorePayloadEncodedDto encodedDto = userStoredKeysService.saveStoredQuestionStoredKey(token.getSubject(), keyId, keyStoredId, body);
        return ResponseEntity.ok(encodedDto);
    }

    @DeleteMapping("{uuid}/key/{keyId}/stored/{keyStoredId}")
    public ResponseEntity<Void> deleteKeyPayload(@AuthenticationPrincipal Jwt token, @PathVariable("uuid") String uuid, @PathVariable("keyId") Long keyId, Long keyStoredId) throws KeyRegistrationErrorException, UserRegistrationNotFoundException, KeyNotFoundException {

        UserRegisteredModel userRegistration = userRegistrationService.getUserRegistration(token.getSubject());
        if (Boolean.FALSE.equals(uuid.equals(userRegistration.getUuid()))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        userStoredKeysService.deleteStoredKey(token.getSubject(), keyId, keyStoredId);
        return ResponseEntity.ok().build();
    }
}
