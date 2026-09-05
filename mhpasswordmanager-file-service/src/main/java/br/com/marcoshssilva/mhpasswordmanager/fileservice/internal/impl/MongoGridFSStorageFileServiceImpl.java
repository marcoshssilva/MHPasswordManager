package br.com.marcoshssilva.mhpasswordmanager.fileservice.internal.impl;

import br.com.marcoshssilva.mhpasswordmanager.fileservice.amqp.queues.FileProcessingWorkerQueue;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.entities.StoredFileKey;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.enums.FileProcessingStatus;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.etc.BucketStoredFile;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.etc.StoredFile;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.repositories.StoredFileKeyRepository;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.internal.IStorageFileService;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.internal.IS3StorageService;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.internal.exceptions.StorageErrorException;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

@Service
public class MongoGridFSStorageFileServiceImpl implements IStorageFileService {
    private static final Clock CLOCK = Clock.systemUTC();
    private static final DateTimeFormatter METADATA_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final StoredFileKeyRepository storedFileKeyRepository;
    private final GridFsTemplate gridFsTemplate;
    private final FileProcessingWorkerQueue processingWorker;
    private final IS3StorageService s3StorageService;

    public MongoGridFSStorageFileServiceImpl(GridFsTemplate gridFsTemplate, StoredFileKeyRepository storedFileKeyRepository, FileProcessingWorkerQueue processingWorker, IS3StorageService s3StorageService) {
        this.gridFsTemplate = gridFsTemplate;
        this.storedFileKeyRepository = storedFileKeyRepository;
        this.processingWorker = processingWorker;
        this.s3StorageService = s3StorageService;
    }

    @Override
    public StoredFile saveFileInStorage(MultipartFile file, String bucketUuid, Map<String, String> metadata) throws StorageErrorException {
        try {
            String fileId = UUID.randomUUID().toString();
            Map<String, String> metadataMap = new HashMap<>(metadata.size() + 1);
            metadataMap.putAll(metadata);
            metadataMap.put("filename", file.getOriginalFilename());
            metadataMap.put("content_type", file.getContentType());
            metadataMap.put("bucket_uuid", bucketUuid);
            LocalDateTime now = LocalDateTime.now(CLOCK);
            metadataMap.put("created_at", METADATA_DATE_FORMATTER.format(now));
            metadataMap.put("updated_at", METADATA_DATE_FORMATTER.format(now));

            Path temporaryFile = Files.createTempFile("mhp-upload-", ".bin");
            try (InputStream input = file.getInputStream()) { Files.copy(input, temporaryFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING); }
            storedFileKeyRepository.save(StoredFileKey.builder().uuid(fileId).bucket(bucketUuid).stagingObjectKey("staging/" + fileId + "/source").metadata(metadataMap).status(FileProcessingStatus.UPLOAD_RECEIVED).ready(Boolean.FALSE).build());
            processingWorker.storeSource(fileId, temporaryFile);
            return StoredFile.builder()
                    .id(fileId)
                    .bucket(bucketUuid)
                    .metadata(metadataMap)
                    .status(FileProcessingStatus.UPLOAD_RECEIVED)
                    .ready(Boolean.FALSE)
                    .build();
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

            StoredFileKey fileKey = storedFileKey.get();
            if (!Boolean.TRUE.equals(fileKey.getReady())) {
                throw new StorageErrorException("File is still being processed.");
            }
            try (InputStream input = s3StorageService.download(fileKey.getS3ObjectKey())) {
                return input.readAllBytes();
            } catch (Exception s3Exception) {
                GridFSFile gridFsFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(new ObjectId(fileKey.getGridFsHex()))));
                if (gridFsFile == null) {
                    throw s3Exception;
                }
                try (InputStream input = gridFsTemplate.getResource(gridFsFile).getInputStream()) {
                    return input.readAllBytes();
                }
            }
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
            return toStoredFile(storedFileKey.get());
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

            StoredFileKey fileKey = storedFileKey.get();
            if (fileKey.getGridFsHex() != null) {
                GridFSFile file = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(new ObjectId(fileKey.getGridFsHex()))));
                if (file != null) {
                    gridFsTemplate.delete(Query.query(Criteria.where("_id").is(file.getId())));
                }
            }
            if (fileKey.getS3ObjectKey() != null) {
                s3StorageService.delete(fileKey.getS3ObjectKey());
            }
            if (fileKey.getStagingObjectKey() != null) {
                s3StorageService.delete(fileKey.getStagingObjectKey());
            }
            storedFileKeyRepository.deleteById(id);

            return Boolean.TRUE;
        } catch (Exception e) {
            throw new StorageErrorException(e.getMessage(), e);
        }
    }

    @Override
    public BucketStoredFile getBucketInfo(String bucketUuid) throws StorageErrorException {
        try {
            Collection<StoredFileKey> bucketStoredFiles = storedFileKeyRepository.findByBucket(bucketUuid);
            return BucketStoredFile.builder().files(bucketStoredFiles.stream().map(this::toStoredFile).collect(Collectors.toCollection(java.util.HashSet::new))).build();
        } catch (Exception e) {
            throw new StorageErrorException(e.getMessage(), e);
        }
    }

    private StoredFile toStoredFile(StoredFileKey file) {
        return StoredFile.builder()
                .id(file.getUuid())
                .bucket(file.getBucket())
                .metadata(file.getMetadata())
                .status(file.getStatus())
                .error(file.getError())
                .ready(file.getReady())
                .build();
    }
}
