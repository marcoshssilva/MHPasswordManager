package br.com.marcoshssilva.mhpasswordmanager.fileservice.amqp.queues;

import br.com.marcoshssilva.mhpasswordmanager.fileservice.Application;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.RabbitMQMockTestConfiguration;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.amqp.models.FileEncryptionCompletedEvent;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.amqp.models.FileEncryptionFailedEvent;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.entities.StoredFileKey;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.enums.FileProcessingStatus;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.repositories.StoredFileKeyRepository;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.internal.IS3StorageService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.test.RabbitListenerTest;
import org.springframework.amqp.rabbit.test.RabbitListenerTestHarness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBootTest(classes = Application.class)
@RabbitListenerTest(spy = true, capture = true)
@Import(RabbitMQMockTestConfiguration.class)
class FileProcessingWorkerQueueListenerTests {

    @Autowired
    private RabbitListenerTestHarness harness;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @MockBean
    private StoredFileKeyRepository repository;

    @MockBean
    private IS3StorageService s3;

    @MockBean
    private GridFsTemplate gridFs;

    @DisplayName("Should capture complete listener invocation via RabbitListenerTestHarness")
    @Test
    void shouldTriggerCompleteEncryptionListener() throws Exception {
        FileEncryptionCompletedEvent event = new FileEncryptionCompletedEvent();
        event.setFileId("file-123");
        event.setEncryptedObjectKey("encrypted/file-123");

        StoredFileKey storedFile = new StoredFileKey();
        storedFile.setUuid("file-123");
        storedFile.setStatus(FileProcessingStatus.ENCRYPTING);
        storedFile.setBucket("bucket-uuid");

        when(repository.findById("file-123")).thenReturn(Optional.of(storedFile));

        rabbitTemplate.convertAndSend(FileProcessingWorkerQueue.ENCRYPTION_COMPLETED, event);

        var invocationData = harness.getNextInvocationDataFor("completeEncryptionListener", 5, TimeUnit.SECONDS);

        assertThat(invocationData).isNotNull();
        assertThat(invocationData.getArguments()[0]).isInstanceOf(FileEncryptionCompletedEvent.class);
    }

    @DisplayName("Should capture encryptionFailed listener invocation via RabbitListenerTestHarness")
    @Test
    void shouldTriggerFailedEncryptionListener() throws Exception {
        FileEncryptionFailedEvent event = new FileEncryptionFailedEvent();
        event.setFileId("file-123");
        event.setError("Encryption error");

        StoredFileKey storedFile = new StoredFileKey();
        storedFile.setUuid("file-123");

        when(repository.findById("file-123")).thenReturn(Optional.of(storedFile));

        rabbitTemplate.convertAndSend(FileProcessingWorkerQueue.ENCRYPTION_FAILED, event);

        var invocationData = harness.getNextInvocationDataFor("failedEncryptionListener", 5, TimeUnit.SECONDS);

        assertThat(invocationData).isNotNull();
        verify(repository).save(argThat(file -> file.getStatus() == FileProcessingStatus.FAILED));
    }
}
