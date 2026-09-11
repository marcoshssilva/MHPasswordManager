package br.com.marcoshssilva.mhpasswordmanager.passwordservice.amqp.queues;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.MhPasswordManagerPasswordServiceApplication;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.RabbitMQMockTestConfiguration;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.amqp.models.FileEncryptionCompletedEvent;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.amqp.models.FileEncryptionFailedEvent;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.amqp.models.FileEncryptionRequestedEvent;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities.UserBucket;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.repositories.UserBucketRepository;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt.CryptService;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.storage.S3StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.test.RabbitListenerTest;
import org.springframework.amqp.rabbit.test.RabbitListenerTestHarness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.ByteArrayInputStream;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@SpringBootTest(classes = MhPasswordManagerPasswordServiceApplication.class)
@RabbitListenerTest(spy = true, capture = true)
@Import(RabbitMQMockTestConfiguration.class)
class FileEncryptionWorkerQueueTests {

    @Autowired
    private RabbitListenerTestHarness harness;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private UserBucketRepository userBucketRepository;

    @MockBean
    private S3StorageService storageService;

    @Autowired
    private FileEncryptionWorkerQueue worker;

    @BeforeEach
    void cleanUp() {
        userBucketRepository.deleteAll();
    }

    @DisplayName("Should successfully encrypt file and publish completion event when called directly")
    @Test
    void shouldEncryptFileDirectly() {
        S3StorageService mockStorage = mock(S3StorageService.class);
        UserBucketRepository mockRepo = mock(UserBucketRepository.class);
        CryptService mockCrypt = mock(CryptService.class);
        AmqpTemplate mockRabbit = mock(AmqpTemplate.class);

        FileEncryptionWorkerQueue directWorker = new FileEncryptionWorkerQueue(mockStorage, mockRepo, mockCrypt, mockRabbit);

        UserBucket bucket = new UserBucket();
        bucket.setId("bucket-1");
        bucket.setEncodedPublicKey("public-key-base64");
        when(mockRepo.findById("bucket-1")).thenReturn(Optional.of(bucket));

        byte[] plainBytes = "hello world".getBytes();
        byte[] encBytes = "encrypted data".getBytes();
        when(mockStorage.download("source-key")).thenReturn(new ResponseInputStream<>(
                GetObjectResponse.builder().build(),
                new ByteArrayInputStream(plainBytes)
        ));
        when(mockCrypt.encrypt(plainBytes, "public-key-base64")).thenReturn(encBytes);

        FileEncryptionRequestedEvent event = new FileEncryptionRequestedEvent();
        event.setFileId("file-1");
        event.setBucketUuid("bucket-1");
        event.setSourceObjectKey("source-key");
        event.setEncryptedObjectKey("enc-key");

        directWorker.encrypt(event);

        verify(mockStorage).upload(eq("enc-key"), any(RequestBody.class));
        ArgumentCaptor<FileEncryptionCompletedEvent> captor = ArgumentCaptor.forClass(FileEncryptionCompletedEvent.class);
        verify(mockRabbit).convertAndSend(eq(FileEncryptionWorkerQueue.EXCHANGE), eq(FileEncryptionWorkerQueue.ENCRYPTION_COMPLETED), captor.capture());
        assertThat(captor.getValue().getFileId()).isEqualTo("file-1");
        assertThat(captor.getValue().getEncryptedObjectKey()).isEqualTo("enc-key");
    }

    @DisplayName("Should publish failure event when bucket is not found")
    @Test
    void shouldPublishFailureWhenBucketNotFound() {
        S3StorageService mockStorage = mock(S3StorageService.class);
        UserBucketRepository mockRepo = mock(UserBucketRepository.class);
        CryptService mockCrypt = mock(CryptService.class);
        AmqpTemplate mockRabbit = mock(AmqpTemplate.class);

        FileEncryptionWorkerQueue directWorker = new FileEncryptionWorkerQueue(mockStorage, mockRepo, mockCrypt, mockRabbit);
        when(mockRepo.findById("bucket-not-found")).thenReturn(Optional.empty());

        FileEncryptionRequestedEvent event = new FileEncryptionRequestedEvent();
        event.setFileId("file-2");
        event.setBucketUuid("bucket-not-found");
        event.setSourceObjectKey("source-key");
        event.setEncryptedObjectKey("enc-key");

        directWorker.encrypt(event);

        ArgumentCaptor<FileEncryptionFailedEvent> captor = ArgumentCaptor.forClass(FileEncryptionFailedEvent.class);
        verify(mockRabbit).convertAndSend(eq(FileEncryptionWorkerQueue.EXCHANGE), eq(FileEncryptionWorkerQueue.ENCRYPTION_FAILED), captor.capture());
        assertThat(captor.getValue().getFileId()).isEqualTo("file-2");
        assertThat(captor.getValue().getError()).contains("Bucket not found");
    }

    @DisplayName("Should receive encryption requested message via listener and process it")
    @Test
    void shouldProcessMessageViaRabbitListener() throws Exception {
        UserBucket bucket = new UserBucket();
        bucket.setId("bucket-listener");
        bucket.setName("Listener Bucket");
        bucket.setDescription("Test");
        bucket.setOwnerName("test-user");
        bucket.setCreatedAt(LocalDateTime.now(Clock.systemUTC()));
        bucket.setLastUpdate(LocalDateTime.now(Clock.systemUTC()));
        bucket.setEncodedPublicKey("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC7");
        bucket.setEncryptedPrivateKeyWithPassword("secret");
        userBucketRepository.save(bucket);

        byte[] plainBytes = "listener test content".getBytes();
        when(storageService.download("staging/file-listen/source")).thenReturn(new ResponseInputStream<>(
                GetObjectResponse.builder().build(),
                new ByteArrayInputStream(plainBytes)
        ));

        FileEncryptionRequestedEvent event = new FileEncryptionRequestedEvent();
        event.setFileId("file-listener-1");
        event.setBucketUuid("bucket-listener");
        event.setSourceObjectKey("staging/file-listen/source");
        event.setEncryptedObjectKey("encrypted/file-listen");

        rabbitTemplate.convertAndSend(FileEncryptionWorkerQueue.ENCRYPTION_REQUESTED, event);

        var invocationData = harness.getNextInvocationDataFor("encryptListener", 5, TimeUnit.SECONDS);
        assertThat(invocationData).isNotNull();
        assertThat(invocationData.getArguments()[0]).isInstanceOf(FileEncryptionRequestedEvent.class);
    }
}
