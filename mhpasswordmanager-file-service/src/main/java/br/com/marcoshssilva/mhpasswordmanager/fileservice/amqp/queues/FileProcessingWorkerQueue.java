package br.com.marcoshssilva.mhpasswordmanager.fileservice.amqp.queues;

import br.com.marcoshssilva.mhpasswordmanager.fileservice.amqp.models.FileEncryptionCompletedEvent;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.amqp.models.FileEncryptionFailedEvent;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.amqp.models.FileEncryptionRequestedEvent;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.entities.StoredFileKey;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.enums.FileProcessingStatus;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.repositories.StoredFileKeyRepository;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.internal.IS3StorageService;

import org.bson.types.ObjectId;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

@Component
public class FileProcessingWorkerQueue {
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
            } catch (Exception ignored) {
            }
        }
    }

    @RabbitListener(queues = FileProcessingWorkerQueue.ENCRYPTION_COMPLETED)
    public void complete(FileEncryptionCompletedEvent event) {
        Path encrypted = null;
        try {
            StoredFileKey file = repository.findById(event.getFileId()).orElseThrow();
            if (file.getStatus() == FileProcessingStatus.READY || file.getStatus() == FileProcessingStatus.FINALIZING) {
                return;
            }
            file.setStatus(FileProcessingStatus.FINALIZING);
            repository.save(file);
            encrypted = Files.createTempFile("mhp-encrypted-", ".bin");
            try (ResponseInputStream<GetObjectResponse> input = s3.download(event.getEncryptedObjectKey())) { Files.copy(input, encrypted, java.nio.file.StandardCopyOption.REPLACE_EXISTING); }
            String finalKey = "files/" + file.getBucket() + "/" + file.getUuid();
            Path finalEncrypted = encrypted;
            CompletableFuture<Void> s3Copy = CompletableFuture.runAsync(() -> s3.upload(finalKey, RequestBody.fromFile(finalEncrypted)));
            CompletableFuture<ObjectId> gridFsCopy = CompletableFuture.supplyAsync(() -> storeGridFsCopy(finalEncrypted, finalKey, file));
            CompletableFuture.allOf(s3Copy, gridFsCopy).join();
            file.setGridFsHex(gridFsCopy.join().toHexString());
            file.setS3ObjectKey(finalKey);
            file.setStatus(FileProcessingStatus.READY);
            file.setReady(Boolean.TRUE);
            file.setError(null);
            repository.save(file);
            deleteQuietly(file.getStagingObjectKey());
            deleteQuietly(event.getEncryptedObjectKey());
        } catch (Exception e) {
            fail(event == null ? null : event.getFileId(), e.getMessage());
        } finally {
            if (encrypted != null) {
                try {
                    Files.deleteIfExists(encrypted);
                } catch (Exception ignored) {
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

    @RabbitListener(queues = FileProcessingWorkerQueue.ENCRYPTION_FAILED)
    public void encryptionFailed(FileEncryptionFailedEvent event) {
        try {
            fail(event.getFileId(), event.getError());
        } catch (Exception ignored) { }
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
        } catch (Exception ignored) {
        }
    }
}
