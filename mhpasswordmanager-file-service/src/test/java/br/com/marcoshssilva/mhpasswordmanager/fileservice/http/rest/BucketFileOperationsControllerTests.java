package br.com.marcoshssilva.mhpasswordmanager.fileservice.http.rest;

import br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.enums.FileProcessingStatus;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.etc.BucketStoredFile;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.etc.StoredFile;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.http.error.ControllerAdviceRestExceptionController;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.internal.IStorageFileService;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.internal.exceptions.StorageErrorException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BucketFileOperationsControllerTests {

    @Mock
    private IStorageFileService storageFileService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        BucketFileOperationsController controller = new BucketFileOperationsController(storageFileService, objectMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ControllerAdviceRestExceptionController())
                .build();
    }

    @DisplayName("Should return 202 ACCEPTED with Location header on createBucketFileKey with metadata")
    @Test
    void shouldReturnAcceptedOnCreateBucketFileKeyWithMetadata() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", MediaType.TEXT_PLAIN_VALUE, "hello world".getBytes(StandardCharsets.UTF_8));
        MockMultipartFile metadata = new MockMultipartFile("metadata", "", MediaType.APPLICATION_JSON_VALUE, "{\"author\":\"john\"}".getBytes(StandardCharsets.UTF_8));

        StoredFile storedFile = StoredFile.builder()
                .id("key-123")
                .bucket("bucket-uuid")
                .status(FileProcessingStatus.UPLOAD_RECEIVED)
                .ready(false)
                .build();

        when(storageFileService.saveFileInStorage(any(), eq("bucket-uuid"), eq(Map.of("author", "john"))))
                .thenReturn(storedFile);

        MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart("/bucket/{bucketUuid}/create-file", "bucket-uuid");
        builder.with(request -> {
            request.setMethod("PUT");
            return request;
        });

        mockMvc.perform(builder.file(file).file(metadata))
                .andExpect(status().isAccepted())
                .andExpect(header().string("Location", "/mypass-manager/files/bucket/bucket-uuid/key-123/info"));

        verify(storageFileService).saveFileInStorage(any(), eq("bucket-uuid"), eq(Map.of("author", "john")));
    }

    @DisplayName("Should return 202 ACCEPTED with Location header on createBucketFileKey without metadata")
    @Test
    void shouldReturnAcceptedOnCreateBucketFileKeyWithoutMetadata() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", MediaType.TEXT_PLAIN_VALUE, "hello world".getBytes(StandardCharsets.UTF_8));

        StoredFile storedFile = StoredFile.builder()
                .id("key-123")
                .bucket("bucket-uuid")
                .status(FileProcessingStatus.UPLOAD_RECEIVED)
                .ready(false)
                .build();

        when(storageFileService.saveFileInStorage(any(), eq("bucket-uuid"), eq(Collections.emptyMap())))
                .thenReturn(storedFile);

        MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart("/bucket/{bucketUuid}/create-file", "bucket-uuid");
        builder.with(request -> {
            request.setMethod("PUT");
            return request;
        });

        mockMvc.perform(builder.file(file))
                .andExpect(status().isAccepted())
                .andExpect(header().string("Location", "/mypass-manager/files/bucket/bucket-uuid/key-123/info"));

        verify(storageFileService).saveFileInStorage(any(), eq("bucket-uuid"), eq(Collections.emptyMap()));
    }

    @DisplayName("Should return 400 BAD_REQUEST when createBucketFileKey encounters StorageErrorException")
    @Test
    void shouldReturnBadRequestOnCreateBucketFileKeyError() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", MediaType.TEXT_PLAIN_VALUE, "hello world".getBytes(StandardCharsets.UTF_8));

        when(storageFileService.saveFileInStorage(any(), eq("bucket-uuid"), any()))
                .thenThrow(new StorageErrorException("Cannot save file."));

        MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart("/bucket/{bucketUuid}/create-file", "bucket-uuid");
        builder.with(request -> {
            request.setMethod("PUT");
            return request;
        });

        mockMvc.perform(builder.file(file))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Cannot save file."));
    }

    @DisplayName("Should return 202 ACCEPTED when updateBucketFileKey is called with metadata")
    @Test
    void shouldReturnAcceptedOnUpdateBucketFileKeyWithMetadata() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", MediaType.TEXT_PLAIN_VALUE, "hello world".getBytes(StandardCharsets.UTF_8));
        MockMultipartFile metadata = new MockMultipartFile("metadata", "", MediaType.APPLICATION_JSON_VALUE, "{\"key\":\"val\"}".getBytes(StandardCharsets.UTF_8));

        StoredFile storedFile = StoredFile.builder()
                .id("key-123")
                .bucket("bucket-uuid")
                .status(FileProcessingStatus.UPLOAD_RECEIVED)
                .ready(false)
                .build();

        when(storageFileService.updateFileInStorage(any(), eq("bucket-uuid"), eq("key-123"), eq(Map.of("key", "val"))))
                .thenReturn(storedFile);

        MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart("/bucket/{bucketUuid}/{key}/put-file", "bucket-uuid", "key-123");
        builder.with(request -> {
            request.setMethod("PUT");
            return request;
        });

        mockMvc.perform(builder
                        .file(file)
                        .file(metadata))
                .andExpect(status().isAccepted());

        verify(storageFileService).updateFileInStorage(any(), eq("bucket-uuid"), eq("key-123"), eq(Map.of("key", "val")));
    }

    @DisplayName("Should return 202 ACCEPTED when updateBucketFileKey is called without metadata")
    @Test
    void shouldReturnAcceptedOnUpdateBucketFileKeyWithoutMetadata() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", MediaType.TEXT_PLAIN_VALUE, "hello world".getBytes(StandardCharsets.UTF_8));

        StoredFile storedFile = StoredFile.builder()
                .id("key-123")
                .bucket("bucket-uuid")
                .status(FileProcessingStatus.UPLOAD_RECEIVED)
                .ready(false)
                .build();

        when(storageFileService.updateFileInStorage(any(), eq("bucket-uuid"), eq("key-123"), eq(Collections.emptyMap())))
                .thenReturn(storedFile);

        MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart("/bucket/{bucketUuid}/{key}/put-file", "bucket-uuid", "key-123");
        builder.with(request -> {
            request.setMethod("PUT");
            return request;
        });

        mockMvc.perform(builder.file(file))
                .andExpect(status().isAccepted());

        verify(storageFileService).updateFileInStorage(any(), eq("bucket-uuid"), eq("key-123"), eq(Collections.emptyMap()));
    }

    @DisplayName("Should return 400 BAD_REQUEST when updateBucketFileKey encounters StorageErrorException")
    @Test
    void shouldReturnBadRequestOnStorageErrorException() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", MediaType.TEXT_PLAIN_VALUE, "hello world".getBytes(StandardCharsets.UTF_8));

        when(storageFileService.updateFileInStorage(any(), eq("bucket-uuid"), eq("not-found-key"), any()))
                .thenThrow(new StorageErrorException("File not found."));

        MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart("/bucket/{bucketUuid}/{key}/put-file", "bucket-uuid", "not-found-key");
        builder.with(request -> {
            request.setMethod("PUT");
            return request;
        });

        mockMvc.perform(builder.file(file))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("File not found."));
    }

    @DisplayName("Should return 200 OK on getBucketInfo")
    @Test
    void shouldReturnBucketInfoSuccessfully() throws Exception {
        StoredFile file1 = StoredFile.builder().id("key-1").bucket("bucket-uuid").status(FileProcessingStatus.READY).ready(true).build();
        BucketStoredFile bucketStoredFile = BucketStoredFile.builder().files(Set.of(file1)).build();

        when(storageFileService.getBucketInfo("bucket-uuid")).thenReturn(bucketStoredFile);

        mockMvc.perform(MockMvcRequestBuilders.get("/bucket/{bucketUuid}/info", "bucket-uuid"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.files[0].id").value("key-1"))
                .andExpect(jsonPath("$.files[0].bucket").value("bucket-uuid"))
                .andExpect(jsonPath("$.files[0].ready").value(true));

        verify(storageFileService).getBucketInfo("bucket-uuid");
    }

    @DisplayName("Should return 400 BAD_REQUEST on getBucketInfo error")
    @Test
    void shouldReturnBadRequestOnGetBucketInfoError() throws Exception {
        when(storageFileService.getBucketInfo("bucket-uuid")).thenThrow(new StorageErrorException("Bucket error."));

        mockMvc.perform(MockMvcRequestBuilders.get("/bucket/{bucketUuid}/info", "bucket-uuid"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Bucket error."));
    }

    @DisplayName("Should return 200 OK with attachment on getBucketFile")
    @Test
    void shouldReturnBucketFileSuccessfully() throws Exception {
        byte[] content = "binary file content".getBytes(StandardCharsets.UTF_8);
        when(storageFileService.getFileInStorage("key-123", "bucket-uuid")).thenReturn(content);

        mockMvc.perform(MockMvcRequestBuilders.get("/bucket/{bucketUuid}/{key}", "bucket-uuid", "key-123"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"key-123\""))
                .andExpect(content().bytes(content));

        verify(storageFileService).getFileInStorage("key-123", "bucket-uuid");
    }

    @DisplayName("Should return 400 BAD_REQUEST on getBucketFile error")
    @Test
    void shouldReturnBadRequestOnGetBucketFileError() throws Exception {
        when(storageFileService.getFileInStorage("key-123", "bucket-uuid")).thenThrow(new StorageErrorException("File not found."));

        mockMvc.perform(MockMvcRequestBuilders.get("/bucket/{bucketUuid}/{key}", "bucket-uuid", "key-123"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("File not found."));
    }

    @DisplayName("Should return 200 OK on getBucketFileInfo")
    @Test
    void shouldReturnBucketFileInfoSuccessfully() throws Exception {
        StoredFile storedFile = StoredFile.builder()
                .id("key-123")
                .bucket("bucket-uuid")
                .metadata(Map.of("filename", "test.txt"))
                .status(FileProcessingStatus.READY)
                .ready(true)
                .build();

        when(storageFileService.getMetadataInStorage("key-123", "bucket-uuid")).thenReturn(storedFile);

        mockMvc.perform(MockMvcRequestBuilders.get("/bucket/{bucketUuid}/{key}/info", "bucket-uuid", "key-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("key-123"))
                .andExpect(jsonPath("$.bucket").value("bucket-uuid"))
                .andExpect(jsonPath("$.metadata.filename").value("test.txt"))
                .andExpect(jsonPath("$.status").value("READY"))
                .andExpect(jsonPath("$.ready").value(true));

        verify(storageFileService).getMetadataInStorage("key-123", "bucket-uuid");
    }

    @DisplayName("Should return 400 BAD_REQUEST on getBucketFileInfo error")
    @Test
    void shouldReturnBadRequestOnGetBucketFileInfoError() throws Exception {
        when(storageFileService.getMetadataInStorage("key-123", "bucket-uuid")).thenThrow(new StorageErrorException("File not found."));

        mockMvc.perform(MockMvcRequestBuilders.get("/bucket/{bucketUuid}/{key}/info", "bucket-uuid", "key-123"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("File not found."));
    }

    @DisplayName("Should return 204 NO_CONTENT on deleteBucketFile")
    @Test
    void shouldReturnNoContentOnDeleteBucketFile() throws Exception {
        when(storageFileService.deleteFileInStorage("key-123", "bucket-uuid")).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.delete("/bucket/{bucketUuid}/{key}", "bucket-uuid", "key-123"))
                .andExpect(status().isNoContent());

        verify(storageFileService).deleteFileInStorage("key-123", "bucket-uuid");
    }

    @DisplayName("Should return 400 BAD_REQUEST on deleteBucketFile error")
    @Test
    void shouldReturnBadRequestOnDeleteBucketFileError() throws Exception {
        when(storageFileService.deleteFileInStorage("key-123", "bucket-uuid")).thenThrow(new StorageErrorException("File not found."));

        mockMvc.perform(MockMvcRequestBuilders.delete("/bucket/{bucketUuid}/{key}", "bucket-uuid", "key-123"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("File not found."));
    }
}
