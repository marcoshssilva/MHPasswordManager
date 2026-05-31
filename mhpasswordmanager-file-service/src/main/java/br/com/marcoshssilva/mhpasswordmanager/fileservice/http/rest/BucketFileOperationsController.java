package br.com.marcoshssilva.mhpasswordmanager.fileservice.http.rest;

import br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.etc.StoredFile;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.internal.IStorageFileService;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.internal.exceptions.StorageErrorException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "Buckets")
@SecurityRequirement(name = "OAuth2 Authorization Code Flow")
@SecurityRequirement(name = "Bearer Authorization")

@RestController
@RequestMapping(("/bucket"))
public class BucketFileOperationsController {
    private final ObjectMapper objectMapper;
    private final IStorageFileService storageFileService;

    public BucketFileOperationsController(IStorageFileService storageFileService, ObjectMapper objectMapper) {
        this.storageFileService = storageFileService;
        this.objectMapper = objectMapper;
    }

    @PutMapping(value = "/{bucketUuid}/put-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateBucketFileKey(@PathVariable String bucketUuid, @RequestPart("file") MultipartFile file, @RequestPart(value = "metadata", required = false) String metadata) throws IOException, StorageErrorException {
        HashMap<String, String> metadataMap = new HashMap<>();
        if (metadata != null) {
            metadataMap.putAll(objectMapper.readValue(metadata, new TypeReference<Map<String, String>>() {
            }));
        }
        StoredFile storedFile = storageFileService.saveFileInStorage(file, bucketUuid, metadataMap);
        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Location", UriComponentsBuilder.fromPath("/mypass-manager/files/bucket/{bucketUuid}/{id}").buildAndExpand(bucketUuid, storedFile.getId()).toUriString())
                .build();
    }

    @GetMapping("/{bucketUuid}/{key}")
    public ResponseEntity<byte[]> getBucketFile(@PathVariable String bucketUuid, @PathVariable String key) throws StorageErrorException {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment; filename=\"" + key + "\"")
                .body(storageFileService.getFileInStorage(key, bucketUuid));
    }

    @GetMapping("/{bucketUuid}/{key}/info")
    public ResponseEntity<StoredFile> getBucketFileInfo(@PathVariable String bucketUuid, @PathVariable String key) throws StorageErrorException {
        StoredFile storedFile = storageFileService.getMetadataInStorage(key, bucketUuid);
        return ResponseEntity.ok(storedFile);
    }

    @DeleteMapping("/{bucketUuid}/{key}")
    public ResponseEntity<Void> deleteBucketFile(@PathVariable String bucketUuid, @PathVariable String key) throws StorageErrorException {
        storageFileService.deleteFileInStorage(key, bucketUuid);
        return ResponseEntity.noContent().build();
    }
}
