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
import br.com.marcoshssilva.mhpasswordmanager.fileservice.utils.FileUtils;

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
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@Service
public class MongoGridFSStorageFileServiceImpl implements IStorageFileService {
    private static final Clock CLOCK = Clock.systemUTC();
    private static final String METADATA_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter METADATA_DATE_FORMATTER = DateTimeFormatter.ofPattern(METADATA_DATE_PATTERN);

    private static final String METADATA_FILENAME = "filename";
    private static final String METADATA_CONTENT_TYPE = "content_type";
    private static final String METADATA_BUCKET_UUID = "bucket_uuid";
    private static final String METADATA_CREATED_AT = "created_at";
    private static final String METADATA_UPDATED_AT = "updated_at";

    private static final String TEMP_FILE_PREFIX = "mhp-upload-";
    private static final String TEMP_FILE_SUFFIX = ".bin";

    private static final String STAGING_PREFIX = "staging/";
    private static final String STAGING_SUFFIX = "/source";

    private static final String ID_FIELD = "_id";

    private static final String ERROR_FILE_NOT_FOUND = "File not found.";
    private static final String ERROR_FILE_STILL_BEING_PROCESSED = "File is still being processed.";

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
        return catchThrowableStorageErrorExceptionWhenFall(() -> {
            String fileId = UUID.randomUUID().toString();
            Map<String, String> metadataMap = new HashMap<>(metadata.size() + 1);

            metadataMap.putAll(metadata);
            metadataMap.put(METADATA_FILENAME, file.getOriginalFilename());
            metadataMap.put(METADATA_CONTENT_TYPE, file.getContentType());
            metadataMap.put(METADATA_BUCKET_UUID, bucketUuid);

            LocalDateTime now = LocalDateTime.now(CLOCK);
            metadataMap.put(METADATA_CREATED_AT, METADATA_DATE_FORMATTER.format(now));
            metadataMap.put(METADATA_UPDATED_AT, METADATA_DATE_FORMATTER.format(now));

            Path temporaryFile = FileUtils.createSecureTempFile(TEMP_FILE_PREFIX, TEMP_FILE_SUFFIX);
            try (InputStream input = file.getInputStream()) { Files.copy(input, temporaryFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING); }

            storedFileKeyRepository.save(StoredFileKey.builder().uuid(fileId).bucket(bucketUuid).stagingObjectKey(STAGING_PREFIX + fileId + STAGING_SUFFIX).metadata(metadataMap).status(FileProcessingStatus.UPLOAD_RECEIVED).ready(Boolean.FALSE).build());
            processingWorker.storeSource(fileId, temporaryFile);

            return StoredFile.builder().id(fileId).bucket(bucketUuid).metadata(metadataMap).status(FileProcessingStatus.UPLOAD_RECEIVED).ready(Boolean.FALSE).build();
        });
    }

    @Override
    public StoredFile updateFileInStorage(MultipartFile file, String bucketUuid, String id, Map<String, String> metadata) throws StorageErrorException {
        return catchThrowableStorageErrorExceptionWhenFall(() -> {
            var fileKey = getStoredFileKey(id, bucketUuid);

            Map<String, String> metadataMap = new HashMap<>();
            if (fileKey.getMetadata() != null) {
                metadataMap.putAll(fileKey.getMetadata());
            }
            if (metadata != null) {
                metadataMap.putAll(metadata);
            }
            metadataMap.put(METADATA_FILENAME, file.getOriginalFilename());
            metadataMap.put(METADATA_CONTENT_TYPE, file.getContentType());
            metadataMap.put(METADATA_BUCKET_UUID, bucketUuid);

            LocalDateTime now = LocalDateTime.now(CLOCK);

            if (!metadataMap.containsKey(METADATA_CREATED_AT)) {
                metadataMap.put(METADATA_CREATED_AT, METADATA_DATE_FORMATTER.format(now));
            }

            metadataMap.put(METADATA_UPDATED_AT, METADATA_DATE_FORMATTER.format(now));

            Path temporaryFile = FileUtils.createSecureTempFile(TEMP_FILE_PREFIX, TEMP_FILE_SUFFIX);
            try (InputStream input = file.getInputStream()) { Files.copy(input, temporaryFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING); }

            fileKey.setMetadata(metadataMap);
            fileKey.setStagingObjectKey(STAGING_PREFIX + id + STAGING_SUFFIX);
            fileKey.setStatus(FileProcessingStatus.UPLOAD_RECEIVED);
            fileKey.setReady(Boolean.FALSE);
            fileKey.setError(null);

            storedFileKeyRepository.save(fileKey);
            processingWorker.storeSource(id, temporaryFile);

            return toStoredFile(fileKey);
        });
    }

    @Override
    public byte[] getFileInStorage(String id, String bucket) throws StorageErrorException {
        return catchThrowableStorageErrorExceptionWhenFall(() -> {
            var fileKey = getStoredFileKey(id, bucket);
            if (!Boolean.TRUE.equals(fileKey.getReady())) {
                throw new StorageErrorException(ERROR_FILE_STILL_BEING_PROCESSED);
            }
            try (InputStream input = s3StorageService.download(fileKey.getS3ObjectKey())) {
                return input.readAllBytes();
            } catch (Exception s3Exception) {
                GridFSFile gridFsFile = gridFsTemplate.findOne(Query.query(Criteria.where(ID_FIELD).is(new ObjectId(fileKey.getGridFsHex()))));
                if (gridFsFile == null) {
                    throw s3Exception;
                }
                try (InputStream input = gridFsTemplate.getResource(gridFsFile).getInputStream()) {
                    return input.readAllBytes();
                }
            }
        });
    }

    @Override
    public StoredFile getMetadataInStorage(String id, String bucket) throws StorageErrorException {
        return catchThrowableStorageErrorExceptionWhenFall(() -> toStoredFile(getStoredFileKey(id, bucket)));
    }

    @Override
    public Boolean deleteFileInStorage(String id, String bucket) throws StorageErrorException {
        return catchThrowableStorageErrorExceptionWhenFall(() -> {
            var fileKey = getStoredFileKey(id, bucket);
            if (fileKey.getGridFsHex() != null) {
                GridFSFile file = gridFsTemplate.findOne(Query.query(Criteria.where(ID_FIELD).is(new ObjectId(fileKey.getGridFsHex()))));
                if (file != null)
                    gridFsTemplate.delete(Query.query(Criteria.where(ID_FIELD).is(file.getId())));
            }
            if (fileKey.getS3ObjectKey() != null) s3StorageService.delete(fileKey.getS3ObjectKey());
            if (fileKey.getStagingObjectKey() != null) s3StorageService.delete(fileKey.getStagingObjectKey());

            storedFileKeyRepository.deleteById(id);

            return Boolean.TRUE;
        });
    }

    @Override
    public BucketStoredFile getBucketInfo(String bucketUuid) throws StorageErrorException {
        return catchThrowableStorageErrorExceptionWhenFall(() -> {
            Collection<StoredFileKey> bucketStoredFiles = storedFileKeyRepository.findByBucket(bucketUuid);
            return BucketStoredFile.builder().files(bucketStoredFiles.stream().map(this::toStoredFile).collect(Collectors.toCollection(java.util.HashSet::new))).build();
        });
    }

    private StoredFile toStoredFile(StoredFileKey file) {
        return StoredFile.builder().id(file.getUuid()).bucket(file.getBucket()).metadata(file.getMetadata()).status(file.getStatus()).error(file.getError()).ready(file.getReady()).build();
    }

    private StoredFileKey getStoredFileKey(String id, String bucket) throws StorageErrorException {
        Optional<StoredFileKey> storedFileKey = storedFileKeyRepository.findByUuidAndBucket(id, bucket);
        if (storedFileKey.isEmpty()) {
            throw new StorageErrorException(ERROR_FILE_NOT_FOUND);
        }
        return storedFileKey.get();
    }

    private static <T> T catchThrowableStorageErrorExceptionWhenFall(Callable<T> executable) throws StorageErrorException {
        try {
            return executable.call();
        } catch (Throwable e) {
            throw new StorageErrorException(e.getMessage(), e);
        }
    }
}
