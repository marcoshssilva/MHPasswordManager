package br.com.marcoshssilva.mhpasswordmanager.fileservice.internal.impl;

import br.com.marcoshssilva.mhpasswordmanager.fileservice.amqp.queues.FileProcessingWorkerQueue;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.entities.StoredFileKey;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.enums.FileProcessingStatus;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.etc.StoredFile;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.repositories.StoredFileKeyRepository;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.internal.IS3StorageService;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.internal.exceptions.StorageErrorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MongoGridFSStorageFileServiceImplTests {

    @Mock
    private StoredFileKeyRepository storedFileKeyRepository;

    @Mock
    private GridFsTemplate gridFsTemplate;

    @Mock
    private FileProcessingWorkerQueue processingWorker;

    @Mock
    private IS3StorageService s3StorageService;

    private MongoGridFSStorageFileServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new MongoGridFSStorageFileServiceImpl(gridFsTemplate, storedFileKeyRepository, processingWorker, s3StorageService);
    }

    @DisplayName("Should throw StorageErrorException when file key does not exist on update")
    @Test
    void shouldThrowStorageErrorExceptionWhenKeyNotFoundOnUpdate() {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "content".getBytes(StandardCharsets.UTF_8));
        when(storedFileKeyRepository.findByUuidAndBucket("non-existent-key", "bucket-123")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateFileInStorage(file, "bucket-123", "non-existent-key", Map.of("desc", "new desc")))
                .isInstanceOf(StorageErrorException.class)
                .hasMessage("File not found.");

        verify(storedFileKeyRepository, never()).save(any());
        verify(processingWorker, never()).storeSource(any(), any());
    }

    @DisplayName("Should update file, metadata, status and queue source for processing")
    @Test
    void shouldUpdateFileInStorageSuccessfully() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "updated.txt", "text/plain", "updated content".getBytes(StandardCharsets.UTF_8));
        String bucketUuid = "bucket-123";
        String key = "file-key-456";

        Map<String, String> existingMetadata = new HashMap<>();
        existingMetadata.put("filename", "old.txt");
        existingMetadata.put("created_at", "2026-01-01 10:00:00");
        existingMetadata.put("author", "john");

        StoredFileKey existingFile = StoredFileKey.builder()
                .uuid(key)
                .bucket(bucketUuid)
                .metadata(existingMetadata)
                .status(FileProcessingStatus.READY)
                .ready(Boolean.TRUE)
                .gridFsHex("hex123")
                .s3ObjectKey("files/bucket-123/file-key-456")
                .build();

        when(storedFileKeyRepository.findByUuidAndBucket(key, bucketUuid)).thenReturn(Optional.of(existingFile));

        Map<String, String> newMetadata = Map.of("author", "jane", "version", "2");
        StoredFile result = service.updateFileInStorage(file, bucketUuid, key, newMetadata);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(key);
        assertThat(result.getBucket()).isEqualTo(bucketUuid);
        assertThat(result.getStatus()).isEqualTo(FileProcessingStatus.UPLOAD_RECEIVED);
        assertThat(result.getReady()).isFalse();
        assertThat(result.getMetadata())
                .containsEntry("filename", "updated.txt")
                .containsEntry("content_type", "text/plain")
                .containsEntry("bucket_uuid", bucketUuid)
                .containsEntry("created_at", "2026-01-01 10:00:00")
                .containsEntry("author", "jane")
                .containsEntry("version", "2")
                .containsKey("updated_at");

        ArgumentCaptor<StoredFileKey> captor = ArgumentCaptor.forClass(StoredFileKey.class);
        verify(storedFileKeyRepository).save(captor.capture());
        StoredFileKey savedFile = captor.getValue();
        assertThat(savedFile.getUuid()).isEqualTo(key);
        assertThat(savedFile.getStatus()).isEqualTo(FileProcessingStatus.UPLOAD_RECEIVED);
        assertThat(savedFile.getReady()).isFalse();
        assertThat(savedFile.getStagingObjectKey()).isEqualTo("staging/" + key + "/source");

        verify(processingWorker).storeSource(eq(key), any());
    }
}
