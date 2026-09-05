package br.com.marcoshssilva.mhpasswordmanager.passwordservice.amqp.queues;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.amqp.models.FileEncryptionCompletedEvent;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.amqp.models.FileEncryptionFailedEvent;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.amqp.models.FileEncryptionRequestedEvent;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities.UserBucket;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.repositories.UserBucketRepository;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt.CryptService;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.storage.S3StorageService;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

@Component
public class FileEncryptionWorkerQueue {
    public static final String EXCHANGE = "files.events";
    public static final String ENCRYPTION_REQUESTED = "file.encryption.requested";
    public static final String ENCRYPTION_COMPLETED = "file.encryption.completed";
    public static final String ENCRYPTION_FAILED = "file.encryption.failed";

    private final S3StorageService storageService;
    private final UserBucketRepository bucketRepository;
    private final CryptService rsaCryptService;
    private final AmqpTemplate rabbitTemplate;

    public FileEncryptionWorkerQueue(S3StorageService storageService, UserBucketRepository bucketRepository, @Qualifier("rsaCryptService") CryptService rsaCryptService, AmqpTemplate rabbitTemplate) {
        this.storageService = storageService; this.bucketRepository = bucketRepository; this.rsaCryptService = rsaCryptService; this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = FileEncryptionWorkerQueue.ENCRYPTION_REQUESTED)
    public void encrypt(FileEncryptionRequestedEvent event) {
        try {
            UserBucket bucket = bucketRepository.findById(event.getBucketUuid()).orElseThrow(() -> new IllegalArgumentException("Bucket not found"));
            byte[] source;
            try (ResponseInputStream<GetObjectResponse> input = storageService.download(event.getSourceObjectKey())) { source = input.readAllBytes(); }
            storageService.upload(event.getEncryptedObjectKey(), RequestBody.fromBytes(rsaCryptService.encrypt(source, bucket.getEncodedPublicKey())));
            FileEncryptionCompletedEvent completed = new FileEncryptionCompletedEvent(); completed.setFileId(event.getFileId()); completed.setEncryptedObjectKey(event.getEncryptedObjectKey());
            rabbitTemplate.convertAndSend(FileEncryptionWorkerQueue.EXCHANGE, FileEncryptionWorkerQueue.ENCRYPTION_COMPLETED, completed);
        } catch (Exception exception) {
            FileEncryptionFailedEvent failed = new FileEncryptionFailedEvent(); failed.setFileId(event == null ? null : event.getFileId()); failed.setError(exception.getMessage());
            rabbitTemplate.convertAndSend(FileEncryptionWorkerQueue.EXCHANGE, FileEncryptionWorkerQueue.ENCRYPTION_FAILED, failed);
        }
    }
}
