package br.com.marcoshssilva.mhpasswordmanager.fileservice.amqp.queues;

import br.com.marcoshssilva.mhpasswordmanager.fileservice.Application;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.MongoMockTestServerConfiguration;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.RabbitMQMockTestConfiguration;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.amqp.models.FileEncryptionCompletedEvent;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.amqp.models.FileEncryptionFailedEvent;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.entities.StoredFileKey;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.enums.FileProcessingStatus;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.repositories.StoredFileKeyRepository;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.internal.IS3StorageService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.test.RabbitListenerTest;
import org.springframework.amqp.rabbit.test.RabbitListenerTestHarness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.ByteArrayInputStream;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBootTest(classes = Application.class)
@RabbitListenerTest(spy = true, capture = true)
@Import({
    RabbitMQMockTestConfiguration.class,
    MongoMockTestServerConfiguration.class
})
class FileProcessingWorkerQueueListenerTests {

    @Autowired
    private RabbitListenerTestHarness harness;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private StoredFileKeyRepository repository;

    @MockBean
    private IS3StorageService s3;

    @BeforeEach
    void clearRepository() {
        repository.deleteAll();
    }

    @DisplayName("Should complete encryption and persist the file as ready")
    @Test
    void shouldTriggerCompleteEncryptionListener() throws Exception {
        FileEncryptionCompletedEvent event = new FileEncryptionCompletedEvent();
        event.setFileId("file-123");
        event.setEncryptedObjectKey("encrypted/file-123");

        StoredFileKey storedFile = new StoredFileKey();
        storedFile.setUuid("file-123");
        storedFile.setStatus(FileProcessingStatus.ENCRYPTING);
        storedFile.setBucket("bucket-uuid");
        storedFile.setStagingObjectKey("staging/file-123");
        repository.save(storedFile);

        when(s3.download("encrypted/file-123")).thenReturn(new ResponseInputStream<>(
            GetObjectResponse.builder().build(),
            new ByteArrayInputStream("encrypted content".getBytes())
        ));

        rabbitTemplate.convertAndSend(FileProcessingWorkerQueue.ENCRYPTION_COMPLETED, event);

        var invocationData = harness.getNextInvocationDataFor("completeEncryptionListener", 5, TimeUnit.SECONDS);

        assertThat(invocationData).isNotNull();
        assertThat(invocationData.getArguments()[0]).isInstanceOf(FileEncryptionCompletedEvent.class);
        assertThat(repository.findById("file-123"))
            .get()
            .satisfies(file -> {
                assertThat(file.getStatus()).isEqualTo(FileProcessingStatus.READY);
                assertThat(file.getReady()).isTrue();
                assertThat(file.getS3ObjectKey()).isEqualTo("files/bucket-uuid/file-123");
                assertThat(file.getGridFsHex()).isNotBlank();
                assertThat(file.getError()).isNull();
            });
        verify(s3).upload(org.mockito.ArgumentMatchers.eq("files/bucket-uuid/file-123"), org.mockito.ArgumentMatchers.any());
        verify(s3).delete("staging/file-123");
        verify(s3).delete("encrypted/file-123");
    }

    @DisplayName("Should mark file as failed when encryption fails")
    @Test
    void shouldTriggerFailedEncryptionListener() throws Exception {
        FileEncryptionFailedEvent event = new FileEncryptionFailedEvent();
        event.setFileId("file-123");
        event.setError("Encryption error");

        StoredFileKey storedFile = new StoredFileKey();
        storedFile.setUuid("file-123");
        storedFile.setStatus(FileProcessingStatus.ENCRYPTING);
        storedFile.setReady(Boolean.TRUE);
        repository.save(storedFile);

        rabbitTemplate.convertAndSend(FileProcessingWorkerQueue.ENCRYPTION_FAILED, event);

        var invocationData = harness.getNextInvocationDataFor("failedEncryptionListener", 5, TimeUnit.SECONDS);

        assertThat(invocationData).isNotNull();
        assertThat(repository.findById("file-123"))
            .get()
            .satisfies(file -> {
                assertThat(file.getStatus()).isEqualTo(FileProcessingStatus.FAILED);
                assertThat(file.getReady()).isFalse();
                assertThat(file.getError()).isEqualTo("Encryption error");
            });
    }
}
