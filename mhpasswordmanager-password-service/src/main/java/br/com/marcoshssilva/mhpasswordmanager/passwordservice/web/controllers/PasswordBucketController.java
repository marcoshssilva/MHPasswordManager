package br.com.marcoshssilva.mhpasswordmanager.passwordservice.web.controllers;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.UserBucketService;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.exceptions.BucketCannotBeCreatedException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.exceptions.BucketNotFoundException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.models.BucketDataModel;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.models.BucketNewDataModel;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.common.IResultData;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.UserAuthorizations;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.UserRegistrationService;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.exceptions.UserAuthorizationCannotBeLoadedException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.exceptions.UserRegistrationDeniedAccessException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.web.data.requests.PasswordBucketControllerCreateBucketRequestBody;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.web.data.responses.PasswordBucketControllerBucketDataResponseBody;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bucket")
@Tag(name = "Buckets")
@SecurityRequirement(name = "OAuth2 Authorization Code Flow")
@SecurityRequirement(name = "Bearer Authorization")

@lombok.RequiredArgsConstructor
public class PasswordBucketController {
    private final UserRegistrationService userRegistrationService;
    private final UserBucketService userBucketService;

    @PostMapping("/create")
    public ResponseEntity<PasswordBucketControllerBucketDataResponseBody> createBucket(@AuthenticationPrincipal Jwt token, @RequestBody PasswordBucketControllerCreateBucketRequestBody payload) throws UserAuthorizationCannotBeLoadedException, UserRegistrationDeniedAccessException, BucketCannotBeCreatedException {
        UserAuthorizations userAuthorizations = userRegistrationService.getUserAuthorizations(token);
        final IResultData<BucketDataModel> result = userBucketService.createBucket(
                BucketNewDataModel.builder()
                        .bucketName(payload.getBucketName())
                        .bucketSecret(payload.getBucketSecret())
                        .bucketDescription(payload.getBucketDescription())
                        .userOwner(userAuthorizations.getUsername())
                        .build(),
                userAuthorizations);
        return ResponseEntity.ok(PasswordBucketControllerBucketDataResponseBody
                .builder()
                .bucketUuid(result.getData().getBucketUuid())
                .bucketName(result.getData().getBucketName())
                .bucketDescription(result.getData().getBucketDescription())
                .lastUpdate(result.getData().getLastUpdate())
                .createdAt(result.getData().getCreatedAt())
                .build());
    }

    @GetMapping()
    public ResponseEntity<Page<PasswordBucketControllerBucketDataResponseBody>> getAllBuckets(@AuthenticationPrincipal Jwt token, @ParameterObject @PageableDefault(size = 500) Pageable pageable) {
        return ResponseEntity.internalServerError().build();
    }

    @GetMapping("/{bucketUuid}")
    public ResponseEntity<PasswordBucketControllerBucketDataResponseBody> getBucket(@AuthenticationPrincipal Jwt token, @PathVariable String bucketUuid) throws UserAuthorizationCannotBeLoadedException, BucketNotFoundException, UserRegistrationDeniedAccessException {
        UserAuthorizations userAuthorizations = userRegistrationService.getUserAuthorizations(token);
        IResultData<BucketDataModel> result = userBucketService.getBucketByUuid(bucketUuid, userAuthorizations);

        return ResponseEntity.ok(PasswordBucketControllerBucketDataResponseBody
                .builder()
                .bucketUuid(result.getData().getBucketUuid())
                .bucketName(result.getData().getBucketName())
                .bucketDescription(result.getData().getBucketDescription())
                .lastUpdate(result.getData().getLastUpdate())
                .createdAt(result.getData().getCreatedAt())
                .build());
    }

    @DeleteMapping("/{bucketUuid}")
    public ResponseEntity<Void> deleteBucket(@AuthenticationPrincipal Jwt token, @PathVariable String bucketUuid) {
        return ResponseEntity.internalServerError().build();
    }
}
