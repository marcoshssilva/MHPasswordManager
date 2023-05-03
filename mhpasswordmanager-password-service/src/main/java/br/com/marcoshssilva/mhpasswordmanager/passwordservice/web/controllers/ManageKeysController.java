package br.com.marcoshssilva.mhpasswordmanager.passwordservice.web.controllers;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.AbstractKeyPayloadDecodedDto;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/keys")
@RequiredArgsConstructor
@Tag(name = "Keys Operations")
@SecurityRequirement(name = "OAuth2 Authorization Code Flow")
@SecurityRequirement(name = "Bearer Authorization")
public class ManageKeysController {

    @GetMapping("{uuid}/get/all")
    public ResponseEntity<Void> getAllKeys(@AuthenticationPrincipal Jwt token, @PathVariable("uuid") String uuid, @ParameterObject @PageableDefault(page = 0, size = 500, sort = {"createdAt DESC"}) Pageable pageable) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @GetMapping("{uuid}/get/{id}")
    public ResponseEntity<Void> getKey(@AuthenticationPrincipal Jwt token, @PathVariable("uuid") String uuid, @PathVariable("id") String id) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @PostMapping("/{uuid}/post/new")
    public ResponseEntity<Void> saveKey(@AuthenticationPrincipal Jwt token, @RequestBody AbstractKeyPayloadDecodedDto payload, @PathVariable("uuid") String uuid) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @PutMapping("/{uuid}/put/{id}")
    public ResponseEntity<Void> updateKey(@AuthenticationPrincipal Jwt token, @PathVariable("uuid") String uuid, @PathVariable("id") String id, @RequestBody AbstractKeyPayloadDecodedDto payload) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @DeleteMapping("/{uuid}/del/{id}")
    public ResponseEntity<Void> deleteKey(@AuthenticationPrincipal Jwt token, @PathVariable("uuid") String uuid, @PathVariable String id) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
