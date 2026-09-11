package br.com.marcoshssilva.mhpasswordmanager.fileservice.internal.impl;

import br.com.marcoshssilva.mhpasswordmanager.fileservice.amqp.queues.FileProcessingWorkerQueue;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.entities.StoredFileKey;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.enums.FileProcessingStatus;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.etc.BucketStoredFile;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.etc.StoredFile;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.repositories.StoredFileKeyRepository;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.internal.IS3StorageService;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.internal.exceptions.StorageErrorException;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.BsonObjectId;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.mock.web.MockMultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

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

    @DisplayName("Should save file in storage successfully and dispatch to processing worker")
    @Test
    void shouldSaveFileInStorageSuccessfully() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "hello world".getBytes(StandardCharsets.UTF_8));
        String bucketUuid = "bucket-123";
        Map<String, String> metadata = Map.of("author", "john");

        StoredFile storedFile = service.saveFileInStorage(file, bucketUuid, metadata);

        assertThat(storedFile).isNotNull();
        assertThat(storedFile.getId()).isNotBlank();
        assertThat(storedFile.getBucket()).isEqualTo(bucketUuid);
        assertThat(storedFile.getStatus()).isEqualTo(FileProcessingStatus.UPLOAD_RECEIVED);
        assertThat(storedFile.getReady()).isFalse();
        assertThat(storedFile.getMetadata())
                .containsEntry("filename", "test.txt")
                .containsEntry("content_type", "text/plain")
                .containsEntry("bucket_uuid", bucketUuid)
                .containsEntry("author", "john")
                .containsKey("created_at")
                .containsKey("updated_at");

        ArgumentCaptor<StoredFileKey> captor = ArgumentCaptor.forClass(StoredFileKey.class);
        verify(storedFileKeyRepository).save(captor.capture());
        StoredFileKey savedKey = captor.getValue();
        assertThat(savedKey.getUuid()).isEqualTo(storedFile.getId());
        assertThat(savedKey.getStatus()).isEqualTo(FileProcessingStatus.UPLOAD_RECEIVED);
        assertThat(savedKey.getStagingObjectKey()).isEqualTo("staging/" + storedFile.getId() + "/source");

        verify(processingWorker).storeSource(eq(storedFile.getId()), any());
    }

    @DisplayName("Should wrap exception in StorageErrorException when saveFileInStorage fails")
    @Test
    void shouldThrowStorageErrorExceptionWhenSaveFails() throws Exception {
        MockMultipartFile file = mock(MockMultipartFile.class);
        when(file.getInputStream()).thenThrow(new IOException("Disk read error"));

        assertThatThrownBy(() -> service.saveFileInStorage(file, "bucket-123", Collections.emptyMap()))
                .isInstanceOf(StorageErrorException.class)
                .hasMessageContaining("Disk read error");
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

    @DisplayName("Should throw StorageErrorException when getFileInStorage key not found")
    @Test
    void shouldThrowStorageErrorExceptionWhenGetFileNotFound() {
        when(storedFileKeyRepository.findByUuidAndBucket("missing-key", "bucket-1")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getFileInStorage("missing-key", "bucket-1"))
                .isInstanceOf(StorageErrorException.class)
                .hasMessage("File not found.");
    }

    @DisplayName("Should throw StorageErrorException when getFileInStorage file is not ready")
    @Test
    void shouldThrowStorageErrorExceptionWhenFileNotReady() {
        StoredFileKey key = StoredFileKey.builder()
                .uuid("key-1")
                .bucket("bucket-1")
                .ready(Boolean.FALSE)
                .build();

        when(storedFileKeyRepository.findByUuidAndBucket("key-1", "bucket-1")).thenReturn(Optional.of(key));

        assertThatThrownBy(() -> service.getFileInStorage("key-1", "bucket-1"))
                .isInstanceOf(StorageErrorException.class)
                .hasMessage("File is still being processed.");
    }

    @DisplayName("Should return bytes from S3 when file is ready and S3 download succeeds")
    @Test
    void shouldReturnBytesFromS3WhenReady() throws Exception {
        byte[] expectedBytes = "file payload".getBytes(StandardCharsets.UTF_8);
        StoredFileKey key = StoredFileKey.builder()
                .uuid("key-1")
                .bucket("bucket-1")
                .ready(Boolean.TRUE)
                .s3ObjectKey("files/bucket-1/key-1")
                .build();

        when(storedFileKeyRepository.findByUuidAndBucket("key-1", "bucket-1")).thenReturn(Optional.of(key));
        ResponseInputStream<GetObjectResponse> stream = new ResponseInputStream<>(
                GetObjectResponse.builder().build(),
                new ByteArrayInputStream(expectedBytes)
        );
        when(s3StorageService.download("files/bucket-1/key-1")).thenReturn(stream);

        byte[] result = service.getFileInStorage("key-1", "bucket-1");

        assertThat(result).isEqualTo(expectedBytes);
        verify(s3StorageService).download("files/bucket-1/key-1");
        verifyNoInteractions(gridFsTemplate);
    }

    @DisplayName("Should fallback to GridFS when S3 download fails")
    @Test
    void shouldFallbackToGridFsWhenS3DownloadFails() throws Exception {
        byte[] expectedBytes = "gridfs payload".getBytes(StandardCharsets.UTF_8);
        ObjectId objectId = new ObjectId();
        StoredFileKey key = StoredFileKey.builder()
                .uuid("key-1")
                .bucket("bucket-1")
                .ready(Boolean.TRUE)
                .s3ObjectKey("files/bucket-1/key-1")
                .gridFsHex(objectId.toHexString())
                .build();

        when(storedFileKeyRepository.findByUuidAndBucket("key-1", "bucket-1")).thenReturn(Optional.of(key));
        when(s3StorageService.download("files/bucket-1/key-1")).thenThrow(new RuntimeException("S3 unavailable"));

        GridFSFile gridFsFile = new GridFSFile(new BsonObjectId(objectId), "test-file", 100L, 256, new Date(), new org.bson.Document());
        GridFsResource gridFsResource = mock(GridFsResource.class);
        when(gridFsTemplate.findOne(any(Query.class))).thenReturn(gridFsFile);
        when(gridFsTemplate.getResource(gridFsFile)).thenReturn(gridFsResource);
        when(gridFsResource.getInputStream()).thenReturn(new ByteArrayInputStream(expectedBytes));

        byte[] result = service.getFileInStorage("key-1", "bucket-1");

        assertThat(result).isEqualTo(expectedBytes);
        verify(gridFsTemplate).findOne(any(Query.class));
    }

    @DisplayName("Should throw StorageErrorException when S3 fails and GridFS file is null")
    @Test
    void shouldThrowStorageErrorExceptionWhenS3FailsAndGridFsNull() {
        ObjectId objectId = new ObjectId();
        StoredFileKey key = StoredFileKey.builder()
                .uuid("key-1")
                .bucket("bucket-1")
                .ready(Boolean.TRUE)
                .s3ObjectKey("files/bucket-1/key-1")
                .gridFsHex(objectId.toHexString())
                .build();

        when(storedFileKeyRepository.findByUuidAndBucket("key-1", "bucket-1")).thenReturn(Optional.of(key));
        when(s3StorageService.download("files/bucket-1/key-1")).thenThrow(new RuntimeException("S3 unavailable"));
        when(gridFsTemplate.findOne(any(Query.class))).thenReturn(null);

        assertThatThrownBy(() -> service.getFileInStorage("key-1", "bucket-1"))
                .isInstanceOf(StorageErrorException.class)
                .hasMessageContaining("S3 unavailable");
    }

    @DisplayName("Should return metadata when file exists")
    @Test
    void shouldReturnMetadataWhenFileExists() throws Exception {
        Map<String, String> meta = Map.of("filename", "photo.png", "created_at", "2026-01-01");
        StoredFileKey fileKey = StoredFileKey.builder()
                .uuid("key-1")
                .bucket("bucket-1")
                .metadata(meta)
                .status(FileProcessingStatus.READY)
                .ready(true)
                .build();

        when(storedFileKeyRepository.findByUuidAndBucket("key-1", "bucket-1")).thenReturn(Optional.of(fileKey));

        StoredFile storedFile = service.getMetadataInStorage("key-1", "bucket-1");

        assertThat(storedFile).isNotNull();
        assertThat(storedFile.getId()).isEqualTo("key-1");
        assertThat(storedFile.getBucket()).isEqualTo("bucket-1");
        assertThat(storedFile.getMetadata()).isEqualTo(meta);
        assertThat(storedFile.getStatus()).isEqualTo(FileProcessingStatus.READY);
        assertThat(storedFile.getReady()).isTrue();
    }

    @DisplayName("Should throw StorageErrorException when metadata file not found")
    @Test
    void shouldThrowStorageErrorExceptionWhenMetadataFileNotFound() {
        when(storedFileKeyRepository.findByUuidAndBucket("key-1", "bucket-1")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getMetadataInStorage("key-1", "bucket-1"))
                .isInstanceOf(StorageErrorException.class)
                .hasMessage("File not found.");
    }

    @DisplayName("Should delete file in storage, GridFS, S3, staging and repository")
    @Test
    void shouldDeleteFileInStorageSuccessfully() throws Exception {
        ObjectId objectId = new ObjectId();
        StoredFileKey fileKey = StoredFileKey.builder()
                .uuid("key-1")
                .bucket("bucket-1")
                .gridFsHex(objectId.toHexString())
                .s3ObjectKey("files/bucket-1/key-1")
                .stagingObjectKey("staging/key-1/source")
                .build();

        when(storedFileKeyRepository.findByUuidAndBucket("key-1", "bucket-1")).thenReturn(Optional.of(fileKey));
        GridFSFile gridFSFile = new GridFSFile(new BsonObjectId(objectId), "test-file", 100L, 256, new Date(), new org.bson.Document());
        when(gridFsTemplate.findOne(any(Query.class))).thenReturn(gridFSFile);

        Boolean result = service.deleteFileInStorage("key-1", "bucket-1");

        assertThat(result).isTrue();
        verify(gridFsTemplate).delete(any(Query.class));
        verify(s3StorageService).delete("files/bucket-1/key-1");
        verify(s3StorageService).delete("staging/key-1/source");
        verify(storedFileKeyRepository).deleteById("key-1");
    }

    @DisplayName("Should delete file when GridFS and S3 keys are null")
    @Test
    void shouldDeleteFileWhenKeysAreNull() throws Exception {
        StoredFileKey fileKey = StoredFileKey.builder()
                .uuid("key-1")
                .bucket("bucket-1")
                .build();

        when(storedFileKeyRepository.findByUuidAndBucket("key-1", "bucket-1")).thenReturn(Optional.of(fileKey));

        Boolean result = service.deleteFileInStorage("key-1", "bucket-1");

        assertThat(result).isTrue();
        verifyNoInteractions(gridFsTemplate);
        verifyNoInteractions(s3StorageService);
        verify(storedFileKeyRepository).deleteById("key-1");
    }

    @DisplayName("Should throw StorageErrorException when deleting non-existent file")
    @Test
    void shouldThrowStorageErrorExceptionWhenDeletingMissingFile() {
        when(storedFileKeyRepository.findByUuidAndBucket("key-1", "bucket-1")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteFileInStorage("key-1", "bucket-1"))
                .isInstanceOf(StorageErrorException.class)
                .hasMessage("File not found.");

        verify(storedFileKeyRepository, never()).deleteById(any());
    }

    @DisplayName("Should get bucket info successfully")
    @Test
    void shouldGetBucketInfoSuccessfully() throws Exception {
        StoredFileKey file1 = StoredFileKey.builder().uuid("f1").bucket("bucket-1").status(FileProcessingStatus.READY).ready(true).build();
        StoredFileKey file2 = StoredFileKey.builder().uuid("f2").bucket("bucket-1").status(FileProcessingStatus.UPLOAD_RECEIVED).ready(false).build();

        when(storedFileKeyRepository.findByBucket("bucket-1")).thenReturn(List.of(file1, file2));

        BucketStoredFile bucketInfo = service.getBucketInfo("bucket-1");

        assertThat(bucketInfo).isNotNull();
        assertThat(bucketInfo.getFiles()).hasSize(2);
        assertThat(bucketInfo.getFiles()).extracting(StoredFile::getId).containsExactlyInAnyOrder("f1", "f2");
    }

    @DisplayName("Should throw StorageErrorException when repository fails on getBucketInfo")
    @Test
    void shouldThrowStorageErrorExceptionWhenGetBucketInfoFails() {
        when(storedFileKeyRepository.findByBucket("bucket-1")).thenThrow(new RuntimeException("DB error"));

        assertThatThrownBy(() -> service.getBucketInfo("bucket-1"))
                .isInstanceOf(StorageErrorException.class)
                .hasMessageContaining("DB error");
    }
}
