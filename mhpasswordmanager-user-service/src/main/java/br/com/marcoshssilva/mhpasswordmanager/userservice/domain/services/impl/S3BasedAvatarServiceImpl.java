package br.com.marcoshssilva.mhpasswordmanager.userservice.domain.services.impl;

import br.com.marcoshssilva.mhpasswordmanager.userservice.configs.AvatarProperties;
import br.com.marcoshssilva.mhpasswordmanager.userservice.configs.S3StorageProperties;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.models.AccountAvatarDataModel;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.repositories.AccountDetailsRepository;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.services.AvatarService;
import br.com.marcoshssilva.mhpasswordmanager.userservice.utils.ImageUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.net.URI;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
public class S3BasedAvatarServiceImpl implements AvatarService {

    private static final int MAX_UPLOAD_ATTEMPTS = 3;
    private static final int MAX_AVATAR_URL_LENGTH = 2048;
    private static final int PRECONDITION_FAILED_STATUS = 412;
    private static final int CONFLICT_STATUS = 409;
    private static final String AVATAR_STORAGE_NOT_CONFIGURED = "Avatar storage is not configured";
    private static final String AVATAR_STORAGE_UNAVAILABLE = "Avatar storage unavailable";
    private static final String AVATAR_ALLOCATION_FAILED = "Could not allocate avatar object";
    private static final String INVALID_PUBLIC_BASE_URL = "Invalid avatar public base URL";
    private static final String AVATAR_URL_TOO_LONG = "Avatar URL exceeds 2048 characters";
    private static final String AVATAR_KEY_PREFIX = "avatar/";
    private static final String AVATAR_URL_PATH = "/mypass-manager/avatars/";
    private static final String PNG_CONTENT_TYPE = "image/png";
    private static final String CREATE_ONLY_CONDITION = "*";
    private static final String AVATAR_REFERENCE_UPDATE_ERROR = "Avatar object {} was saved but its account reference could not be updated";

    private final AccountDetailsRepository repository;
    private final S3Client s3;
    private final S3StorageProperties s3StorageProperties;
    private final AvatarProperties avatarProperties;

    @Override
    public AccountAvatarDataModel upload(String username, MultipartFile file) {
        ensureAccountExists(username);
        byte[] png = ImageUtils.createAvatar(file);
        ensureStorageIsConfigured();
        String baseUrl = getValidatedPublicBaseUrl();

        for (int attempt = 0; attempt < MAX_UPLOAD_ATTEMPTS; attempt++) {
            String filename = UUID.randomUUID().toString().concat(".png");
            String bucketKey = AVATAR_KEY_PREFIX.concat(filename);
            URI url = createAvatarUrl(baseUrl, filename);
            if (!uploadAvatarToS3(png, bucketKey)) {
                continue;
            }
            updateAccountAvatar(username, url, bucketKey);
            return new AccountAvatarDataModel(username, url);
        }

        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, AVATAR_ALLOCATION_FAILED);
    }

    private void ensureAccountExists(String username) {
        if (repository.getAccountDetailsByIdUsername(username).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    private void ensureStorageIsConfigured() {
        if (s3StorageProperties.getBucket().isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, AVATAR_STORAGE_NOT_CONFIGURED);
        }
    }

    private String getValidatedPublicBaseUrl() {
        String baseUrl = avatarProperties.getDefaultPublicBaseUrl().replaceAll("/+$", "");
        URI baseUri = URI.create(baseUrl);
        String scheme = baseUri.getScheme();
        if (!baseUri.isAbsolute() || baseUri.getHost() == null
                || !("https".equals(scheme) || "http".equals(scheme))
                || baseUri.getQuery() != null || baseUri.getFragment() != null || baseUri.getUserInfo() != null) {
            throw new IllegalStateException(INVALID_PUBLIC_BASE_URL);
        }
        return baseUrl;
    }

    private URI createAvatarUrl(String baseUrl, String filename) {
        URI url = URI.create(baseUrl + AVATAR_URL_PATH + filename);
        if (url.toASCIIString().length() > MAX_AVATAR_URL_LENGTH) {
            throw new IllegalStateException(AVATAR_URL_TOO_LONG);
        }
        return url;
    }

    private boolean uploadAvatarToS3(byte[] png, String bucketKey) {
        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(s3StorageProperties.getBucket())
                    .key(bucketKey)
                    .contentType(PNG_CONTENT_TYPE)
                    .ifNoneMatch(CREATE_ONLY_CONDITION)
                    .build();
            s3.putObject(request, RequestBody.fromBytes(png));
            return true;
        } catch (S3Exception e) {
            if (e.statusCode() == PRECONDITION_FAILED_STATUS || e.statusCode() == CONFLICT_STATUS) {
                return false;
            }
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, AVATAR_STORAGE_UNAVAILABLE, e);
        } catch (SdkException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, AVATAR_STORAGE_UNAVAILABLE, e);
        }
    }

    private void updateAccountAvatar(String username, URI url, String bucketKey) {
        try {
            if (repository.updateImageUrlByUsername(username, url.toASCIIString()) != 1) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
        } catch (RuntimeException e) {
            log.error(AVATAR_REFERENCE_UPDATE_ERROR, bucketKey, e);
            throw e;
        }
    }
}
