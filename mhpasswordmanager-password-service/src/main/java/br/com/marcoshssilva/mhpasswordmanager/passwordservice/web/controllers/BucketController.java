package br.com.marcoshssilva.mhpasswordmanager.passwordservice.web.controllers;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.UserBucketService;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.models.BucketDataModel;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.models.BucketNewDataModel;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.models.BucketUpdateDataModel;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.common.IResultData;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.UserAuthorizations;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.UserRegistrationService;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.web.data.requests.PasswordBucketControllerCreateBucketRequestBody;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.web.data.requests.PasswordBucketControllerUpdateBucketRequestBody;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bucket")
@Tag(name = "Buckets")
@SecurityRequirement(name = "OAuth2 Authorization Code Flow")
@SecurityRequirement(name = "Bearer Authorization")

@lombok.RequiredArgsConstructor
public class BucketController {
    private final UserRegistrationService userRegistrationService;
    private final UserBucketService userBucketService;

    @PostMapping("/create")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<PasswordBucketControllerBucketDataResponseBody> createBucket(@AuthenticationPrincipal Jwt token, @RequestBody PasswordBucketControllerCreateBucketRequestBody payload) throws Exception {
        UserAuthorizations userAuthorizations = userRegistrationService.getUserAuthorizations(token);
        final IResultData<BucketDataModel> result = userBucketService.createBucket(
                BucketNewDataModel.builder()
                        .bucketName(payload.getBucketName()).bucketSecret(payload.getBucketSecret()).bucketDescription(payload.getBucketDescription())
                        .userOwner(userAuthorizations.getUsername()).build(),
                userAuthorizations);
        result.throwErrorIfExists();

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
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Page<PasswordBucketControllerBucketDataResponseBody>> getAllBuckets(@AuthenticationPrincipal Jwt token, @ParameterObject @PageableDefault(size = 500) Pageable pageable) throws Exception {
        UserAuthorizations userAuthorizations = userRegistrationService.getUserAuthorizations(token);
        final IResultData<Page<BucketDataModel>> result = userBucketService.getBucketsByUserAuthorizations(userAuthorizations, pageable);
        result.throwErrorIfExists();

        final Page<PasswordBucketControllerBucketDataResponseBody> body = result.getData().map(item -> PasswordBucketControllerBucketDataResponseBody.builder()
                .bucketUuid(item.getBucketUuid()).bucketName(item.getBucketName()).bucketDescription(item.getBucketDescription()).createdAt(item.getCreatedAt()).lastUpdate(item.getLastUpdate())
                .build());

        return ResponseEntity.ok(body);
    }

    @GetMapping("/{bucketUuid}")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<PasswordBucketControllerBucketDataResponseBody> getBucket(@AuthenticationPrincipal Jwt token, @PathVariable String bucketUuid) throws Exception {
        UserAuthorizations userAuthorizations = userRegistrationService.getUserAuthorizations(token);
        IResultData<BucketDataModel> result = userBucketService.getBucketByUuid(bucketUuid, userAuthorizations);
        result.throwErrorIfExists();

        final PasswordBucketControllerBucketDataResponseBody body = PasswordBucketControllerBucketDataResponseBody.builder().bucketUuid(result.getData().getBucketUuid())
                .bucketName(result.getData().getBucketName()).bucketDescription(result.getData().getBucketDescription())
                .lastUpdate(result.getData().getLastUpdate()).createdAt(result.getData().getCreatedAt())
                .build();

        return ResponseEntity.ok(body);
    }

    @DeleteMapping("/{bucketUuid}")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Void> deleteBucket(@AuthenticationPrincipal Jwt token, @PathVariable String bucketUuid) throws Exception {
        UserAuthorizations userAuthorizations = userRegistrationService.getUserAuthorizations(token);
        IResultData<Boolean> result = userBucketService.deleteBucketByUuid(bucketUuid, userAuthorizations);
        result.throwErrorIfExists();
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{bucketUuid}")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<PasswordBucketControllerBucketDataResponseBody> updateBucket(@AuthenticationPrincipal Jwt token, @PathVariable String bucketUuid, @RequestBody PasswordBucketControllerUpdateBucketRequestBody payload) throws Exception {
        UserAuthorizations userAuthorizations = userRegistrationService.getUserAuthorizations(token);
        final IResultData<BucketDataModel> result = userBucketService.updateBucket(
                bucketUuid,
                BucketUpdateDataModel.builder()
                        .bucketName(payload.getBucketName()).bucketDescription(payload.getBucketDescription())
                        .build(),
                userAuthorizations);
        result.throwErrorIfExists();
        return ResponseEntity.ok(PasswordBucketControllerBucketDataResponseBody.builder().bucketUuid(result.getData().getBucketUuid()).bucketName(result.getData().getBucketName()).bucketDescription(result.getData().getBucketDescription()).lastUpdate(result.getData().getLastUpdate()).createdAt(result.getData().getCreatedAt())
                .build());
    }
}
