package br.com.marcoshssilva.mhpasswordmanager.fileservice.http.rest;

import br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.enums.FileProcessingStatus;
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
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

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

    @DisplayName("Should return 202 ACCEPTED when updateBucketFileKey is called")
    @Test
    void shouldReturnAcceptedOnUpdateBucketFileKey() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", MediaType.TEXT_PLAIN_VALUE, "hello world".getBytes());
        MockMultipartFile metadata = new MockMultipartFile("metadata", "", MediaType.APPLICATION_JSON_VALUE, "{\"key\":\"val\"}".getBytes());

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

    @DisplayName("Should return 400 BAD_REQUEST when updateBucketFileKey encounters StorageErrorException")
    @Test
    void shouldReturnBadRequestOnStorageErrorException() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", MediaType.TEXT_PLAIN_VALUE, "hello world".getBytes());

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
}
