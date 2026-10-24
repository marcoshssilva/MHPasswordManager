package br.com.marcoshssilva.mhpasswordmanager.fileservice.amqp.queues;

import br.com.marcoshssilva.mhpasswordmanager.fileservice.amqp.exceptions.MustAbortMessageQueueException;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.amqp.models.FileEncryptionCompletedEvent;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.amqp.models.FileEncryptionFailedEvent;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.amqp.models.FileEncryptionRequestedEvent;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.entities.StoredFileKey;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.enums.FileProcessingStatus;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.repositories.StoredFileKeyRepository;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.internal.IS3StorageService;

import br.com.marcoshssilva.mhpasswordmanager.fileservice.utils.FileUtils;
import org.bson.types.ObjectId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

@Component
public class FileProcessingWorkerQueue {
    public static final Logger LOGGER = LoggerFactory.getLogger(FileProcessingWorkerQueue.class);
    public static final String EXCHANGE = "files.events";
    public static final String ENCRYPTION_REQUESTED = "file.encryption.requested";
    public static final String ENCRYPTION_COMPLETED = "file.encryption.completed";
    public static final String ENCRYPTION_FAILED = "file.encryption.failed";

    private final StoredFileKeyRepository repository;
    private final IS3StorageService s3;
    private final AmqpTemplate rabbit;
    private final GridFsTemplate gridFs;

    public FileProcessingWorkerQueue(StoredFileKeyRepository repository, IS3StorageService s3, AmqpTemplate rabbit, GridFsTemplate gridFs) {
        this.repository = repository;
        this.s3 = s3;
        this.rabbit = rabbit;
        this.gridFs = gridFs;
    }

    @Async
    public void storeSource(String fileId, Path source) {
        try {
            StoredFileKey file = repository.findById(fileId).orElseThrow();
            s3.upload(file.getStagingObjectKey(), RequestBody.fromFile(source));
            file.setStatus(FileProcessingStatus.ENCRYPTING);
            repository.save(file);
            FileEncryptionRequestedEvent event = new FileEncryptionRequestedEvent();
            event.setFileId(fileId);
            event.setBucketUuid(file.getBucket());
            event.setSourceObjectKey(file.getStagingObjectKey());
            event.setEncryptedObjectKey("encrypted/" + fileId);
            rabbit.convertAndSend(FileProcessingWorkerQueue.EXCHANGE, FileProcessingWorkerQueue.ENCRYPTION_REQUESTED, event);
        } catch (Exception e) {
            fail(fileId, e.getMessage());
        } finally {
            try {
                Files.deleteIfExists(source);
            } catch (Exception e) {
                LOGGER.error("Error while deleting source {}", source, e);
            }
        }
    }

    @RabbitListener(id = "completeEncryptionListener", queues = FileProcessingWorkerQueue.ENCRYPTION_COMPLETED)
    public void complete(FileEncryptionCompletedEvent event) {
        Path encrypted = null;
        StoredFileKey file = null;
        String oldGridFsHex = null;
        String finalKey = null;

        try {
            file = repository.findById(event.getFileId()).orElseThrow(() -> new RuntimeException("File not found"));

            cancelIfStatusFinishedOrReady(file);
            updateStatusAsFinalizing(file);

            finalKey = "files/" + file.getBucket() + "/" + file.getUuid();
            encrypted = downloadFileAsPath(event);
            oldGridFsHex = file.getGridFsHex();

            encryptAndUploadFile(finalKey, file, encrypted);
            deleteFromGridFs(oldGridFsHex, file);
            deleteQuietly(file.getStagingObjectKey());
            deleteQuietly(event.getEncryptedObjectKey());

        } catch (MustAbortMessageQueueException e1) {
            LOGGER.error(e1.getMessage());
        } catch (Exception e2) {
            fail(event.getFileId(), e2.getMessage());
        } finally {
            if (encrypted != null) {
                try { Files.deleteIfExists(encrypted); }
                catch (Exception e) {
                    LOGGER.error("Fatal error when trying delete file {} - Message: {}", encrypted, e.getMessage(), e);
                }
            }
        }
    }

    private ObjectId storeGridFsCopy(Path encrypted, String finalKey, StoredFileKey file) {
        try (InputStream input = Files.newInputStream(encrypted)) {
            return gridFs.store(input, finalKey, "application/octet-stream", file.getMetadata());
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to store GridFS backup", exception);
        }
    }

    @RabbitListener(id = "failedEncryptionListener", queues = FileProcessingWorkerQueue.ENCRYPTION_FAILED)
    public void encryptionFailed(FileEncryptionFailedEvent event) {
        try {
            fail(event.getFileId(), event.getError());
        } catch (Exception e) {
            LOGGER.error("Error while encrypting file {} - Message: {}", event.getFileId(), e.getMessage(), e);
        }
    }

    private void fail(String id, String error) {
        if (id == null) {
            return;
        }
        repository.findById(id).ifPresent(file -> {
            file.setStatus(FileProcessingStatus.FAILED);
            file.setReady(Boolean.FALSE);
            file.setError(error);
            repository.save(file);
        });
    }

    private void deleteQuietly(String objectKey) {
        try {
            s3.delete(objectKey);
        } catch (Exception e) {
            LOGGER.error("Error while deleting file {} - Message: {}", objectKey, e.getMessage(), e);
        }
    }

    private void cancelIfStatusFinishedOrReady(StoredFileKey file) {
        FileProcessingStatus[] statuses = {FileProcessingStatus.READY, FileProcessingStatus.FINALIZING};
        if (Arrays.stream(statuses).anyMatch(fps -> fps.equals(file.getStatus()))) {
            throw new MustAbortMessageQueueException("File " + file.getUuid() + " has been cancelled because status are " + file.getStatus());
        }
    }

    private void updateStatusAsFinalizing(StoredFileKey file) {
        file.setStatus(FileProcessingStatus.FINALIZING);
        repository.save(file);
    }

    private Path downloadFileAsPath(FileEncryptionCompletedEvent event) throws IOException {
        try (ResponseInputStream<GetObjectResponse> input = s3.download(event.getEncryptedObjectKey())) {
            Path tempFile = FileUtils.createSecureTempFile("mhp-encryped-", ".bin");
            Files.copy(input, tempFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            return tempFile;
        }
    }

    private void encryptAndUploadFile(String finalKey, StoredFileKey file, Path source) {
        // upload and store file in S3 bucket
        CompletableFuture<Void> s3Copy = CompletableFuture.runAsync(() -> s3.upload(finalKey, RequestBody.fromFile(source)));
        CompletableFuture<ObjectId> gridFsCopy = CompletableFuture.supplyAsync(() -> storeGridFsCopy(source, finalKey, file));
        CompletableFuture.allOf(s3Copy, gridFsCopy).join();

        // update key in MongoDB
        file.setGridFsHex(gridFsCopy.join().toHexString());
        file.setS3ObjectKey(finalKey);
        file.setStatus(FileProcessingStatus.READY);
        file.setReady(Boolean.TRUE);
        file.setError(null);

        repository.save(file);
    }

    private void deleteFromGridFs(String key, StoredFileKey file) {
        if (key != null && !key.equals(file.getGridFsHex())) {
            try {
                gridFs.delete(Query.query(Criteria.where("_id").is(new ObjectId(key))));
            } catch (Exception e) {
                LOGGER.error("Unable to delete file {} from GridFS - Message: {}", file.getUuid(), e.getMessage(), e);
            }
        }
    }
}
