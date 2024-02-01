package br.com.marcoshssilva.mhpasswordmanager.passwordservice.web.controllers;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.web.data.requests.PasswordBucketControllerCreateBucketRequestBody;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.web.data.responses.PasswordBucketControllerBucketDataResponseBody;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@Tag(name = "Buckets")
@SecurityRequirement(name = "OAuth2 Authorization Code Flow")
@SecurityRequirement(name = "Bearer Authorization")
public class PasswordBucketController {

    @PostMapping("/create")
    public ResponseEntity<PasswordBucketControllerBucketDataResponseBody> createBucket(@AuthenticationPrincipal Jwt token, @RequestBody PasswordBucketControllerCreateBucketRequestBody payload) {
        return ResponseEntity.internalServerError().build();
    }

    @GetMapping()
    public ResponseEntity<Page<PasswordBucketControllerBucketDataResponseBody>> getAllBuckets(@AuthenticationPrincipal Jwt token, @ParameterObject @PageableDefault(size = 500) Pageable pageable) {
        return ResponseEntity.internalServerError().build();
    }

    @GetMapping("/{bucketUuid}")
    public ResponseEntity<PasswordBucketControllerBucketDataResponseBody> getBucket(@AuthenticationPrincipal Jwt token, @PathVariable String bucketUuid) {
        return ResponseEntity.internalServerError().build();
    }

    @DeleteMapping("/{bucketUuid}")
    public ResponseEntity<Void> deleteBucket(@AuthenticationPrincipal Jwt token, @PathVariable String bucketUuid) {
        return ResponseEntity.internalServerError().build();
    }
}
