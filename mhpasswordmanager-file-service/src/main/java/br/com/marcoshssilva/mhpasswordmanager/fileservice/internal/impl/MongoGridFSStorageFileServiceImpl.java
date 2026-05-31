package br.com.marcoshssilva.mhpasswordmanager.fileservice.internal.impl;

import br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.client.PasswordServiceClient;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.client.entities.DecryptKeyBase64Payload;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.entities.StoredFileKey;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.etc.StoredFile;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.repositories.StoredFileKeyRepository;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.internal.IStorageFileService;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.internal.exceptions.StorageErrorException;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class MongoGridFSStorageFileServiceImpl implements IStorageFileService {

    private StoredFileKeyRepository storedFileKeyRepository;
    private PasswordServiceClient passwordServiceClient;
    private GridFsTemplate gridFsTemplate;
    private HttpServletRequest request;

    public MongoGridFSStorageFileServiceImpl(GridFsTemplate gridFsTemplate, HttpServletRequest request, StoredFileKeyRepository storedFileKeyRepository, PasswordServiceClient passwordServiceClient) {
        this.gridFsTemplate = gridFsTemplate;
        this.request = request;
        this.storedFileKeyRepository = storedFileKeyRepository;
        this.passwordServiceClient = passwordServiceClient;
    }

    @Override
    public StoredFile saveFileInStorage(MultipartFile file, String bucketUuid, Map<String, String> metadata) throws StorageErrorException {
        try {
            /**
             * Get token reusing from request header. Refactor in future to use any other authentication method.
             */
            String bearerToken = request.getHeader("Authorization");
            if (Objects.isNull(bearerToken) || !bearerToken.startsWith("Bearer ")) {
                throw new StorageErrorException("Credentials are not set.");
            }

            /**
             * Generating random filename and content type from UUID.
             */
            String filename = bucketUuid.concat(UUID.randomUUID().toString());
            String contentType = "application/octet-stream";

            /**
             * Put file metadata in a map.
             */
            Map<String, String> metadataMap = new HashMap<>(metadata.size() + 1);
            metadataMap.putAll(metadata);
            metadataMap.put("filename", file.getOriginalFilename());
            metadataMap.put("content_type", file.getContentType());
            metadataMap.put("bucket_uuid", bucketUuid);
            metadataMap.put("created_at", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now()));
            metadataMap.put("updated_at", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now()));

            /**
             * Calling password-service to encrypt and save in database. Refactor in future to save asynchronously and return a future.
             */
            DecryptKeyBase64Payload response = passwordServiceClient.encryptFileUsingBucket(bearerToken, bucketUuid, file).block(Duration.ofSeconds(300));
            InputStream inputStream = new ByteArrayInputStream(response.getData().getBytes(StandardCharsets.UTF_8));
            ObjectId objectId = gridFsTemplate.store(inputStream, filename, contentType, metadataMap);
            storedFileKeyRepository.save(StoredFileKey.builder().gridFsHex(objectId.toHexString()).metadata(metadataMap).bucket(bucketUuid).ready(Boolean.TRUE).build());

            return StoredFile.builder().id(objectId.toHexString()).metadata(metadataMap).build();
        } catch (Exception e) {
            throw new StorageErrorException(e.getMessage(), e);
        }
    }

    @Override
    public byte[] getFileInStorage(String id, String bucket) throws StorageErrorException {
        try {
            Optional<StoredFileKey> storedFileKey = storedFileKeyRepository.findByUuidAndBucket(id, bucket);
            if (storedFileKey.isEmpty()) {
                throw new StorageErrorException("File not found.");
            }

            GridFSFile file = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(new ObjectId(storedFileKey.get().getGridFsHex()))));
            if (file == null) {
                throw new StorageErrorException("File not found.");
            }

            return gridFsTemplate.getResource(file).getInputStream().readAllBytes();
        } catch (Exception e) {
            throw new StorageErrorException(e.getMessage(), e);
        }
    }


    @Override
    public StoredFile getMetadataInStorage(String id, String bucket) throws StorageErrorException {
        try {
            Optional<StoredFileKey> storedFileKey = storedFileKeyRepository.findByUuidAndBucket(id, bucket);
            if (storedFileKey.isEmpty()) {
                throw new StorageErrorException("File not found.");
            }
            return StoredFile.builder().id(id).bucket(bucket).metadata(storedFileKey.get().getMetadata()).build();
        } catch (Exception e) {
            throw new StorageErrorException(e.getMessage(), e);
        }
    }

    @Override
    public Boolean deleteFileInStorage(String id, String bucket) throws StorageErrorException {
        try {
            Optional<StoredFileKey> storedFileKey = storedFileKeyRepository.findByUuidAndBucket(id, bucket);
            if (storedFileKey.isEmpty()) {
                throw new StorageErrorException("File not found.");
            }

            GridFSFile file = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(new ObjectId(storedFileKey.get().getGridFsHex()))));
            if (file != null) {
                gridFsTemplate.delete(Query.query(Criteria.where("_id").is(file.getId())));
            }
            storedFileKeyRepository.deleteById(storedFileKey.get().getUuid());

            return Boolean.TRUE;
        } catch (Exception e) {
            throw new StorageErrorException(e.getMessage(), e);
        }
    }
}
