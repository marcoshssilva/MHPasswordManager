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

    private final AccountDetailsRepository repository;
    private final S3Client s3;
    private final S3StorageProperties s3StorageProperties;
    private final AvatarProperties avatarProperties;

    @Override
    public AccountAvatarDataModel upload(String username, MultipartFile file) {
        if (repository.getAccountDetailsByIdUsername(username).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        byte[] png = ImageUtils.createAvatar(file);
        if (s3StorageProperties.getBucket().isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Avatar storage is not configured");
        }
        String base = avatarProperties.getDefaultPublicBaseUrl().replaceAll("/+$", "");
        URI baseUri = URI.create(base);
        if (!baseUri.isAbsolute() || baseUri.getHost() == null
                || !("https".equals(baseUri.getScheme()) || "http".equals(baseUri.getScheme()))
                || baseUri.getQuery() != null || baseUri.getFragment() != null || baseUri.getUserInfo() != null) {
            throw new IllegalStateException("Invalid avatar public base URL");
        }
        for (int attempt = 0; attempt < 3; attempt++) {
            String filename = UUID.randomUUID().toString().concat(".png");
            String bucketKey = "avatar/" + UUID.randomUUID() + ".png";
            URI url = URI.create(base + "/mypass-manager/avatars/" + filename);

            if (url.toASCIIString().length() > 2048) throw new IllegalStateException("Avatar URL exceeds 2048 characters");
            try {
                s3.putObject(PutObjectRequest.builder().bucket(s3StorageProperties.getBucket()).key(bucketKey).contentType("image/png").ifNoneMatch("*").build(), RequestBody.fromBytes(png));
            } catch (S3Exception e) {
                if (e.statusCode() == 412 || e.statusCode() == 409) continue;
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Avatar storage unavailable", e);
            } catch (SdkException e) {
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Avatar storage unavailable", e);
            }
            try {
                if (repository.updateImageUrlByUsername(username, url.toASCIIString()) != 1) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND);
                }
            } catch (RuntimeException e) {
                log.error("Avatar object {} was saved but its account reference could not be updated", bucketKey, e);
                throw e;
            }
            return new AccountAvatarDataModel(username, url);
        }
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Could not allocate avatar object");
    }
}
