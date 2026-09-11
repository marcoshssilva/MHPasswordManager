package br.com.marcoshssilva.mhpasswordmanager.fileservice.internal.impl;

import br.com.marcoshssilva.mhpasswordmanager.fileservice.configs.S3StorageProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3StorageServiceImplTests {

    @Mock
    private S3StorageProperties properties;

    @Mock
    private S3Client s3Client;

    @Mock
    private S3Presigner s3Presigner;

    private S3StorageServiceImpl s3StorageService;

    private static final String BUCKET_NAME = "test-bucket";

    @BeforeEach
    void setUp() {
        s3StorageService = new S3StorageServiceImpl(properties, s3Client, s3Presigner);
    }

    @DisplayName("Should return bucket name from properties")
    @Test
    void shouldReturnBucketName() {
        when(properties.getBucket()).thenReturn(BUCKET_NAME);

        String bucketName = s3StorageService.getBucketName();

        assertThat(bucketName).isEqualTo(BUCKET_NAME);
        verify(properties).getBucket();
    }

    @DisplayName("Should get object metadata from S3")
    @Test
    void shouldGetObjectMetadata() {
        when(properties.getBucket()).thenReturn(BUCKET_NAME);
        HeadObjectResponse expectedResponse = HeadObjectResponse.builder()
                .contentLength(1024L)
                .contentType("application/json")
                .build();

        when(s3Client.headObject(any(HeadObjectRequest.class))).thenReturn(expectedResponse);

        HeadObjectResponse response = s3StorageService.getObjectMetadata("file/path/test.json");

        assertThat(response).isEqualTo(expectedResponse);

        ArgumentCaptor<HeadObjectRequest> captor = ArgumentCaptor.forClass(HeadObjectRequest.class);
        verify(s3Client).headObject(captor.capture());
        HeadObjectRequest request = captor.getValue();
        assertThat(request.bucket()).isEqualTo(BUCKET_NAME);
        assertThat(request.key()).isEqualTo("file/path/test.json");
    }

    @DisplayName("Should download object from S3")
    @Test
    void shouldDownloadObject() throws IOException {
        when(properties.getBucket()).thenReturn(BUCKET_NAME);
        byte[] content = "test content".getBytes();
        ResponseInputStream<GetObjectResponse> stream = new ResponseInputStream<>(
                GetObjectResponse.builder().build(),
                new ByteArrayInputStream(content)
        );

        when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(stream);

        ResponseInputStream<GetObjectResponse> resultStream = s3StorageService.download("file/path/download.txt");

        assertThat(resultStream).isNotNull();
        assertThat(resultStream.readAllBytes()).isEqualTo(content);

        ArgumentCaptor<GetObjectRequest> captor = ArgumentCaptor.forClass(GetObjectRequest.class);
        verify(s3Client).getObject(captor.capture());
        GetObjectRequest request = captor.getValue();
        assertThat(request.bucket()).isEqualTo(BUCKET_NAME);
        assertThat(request.key()).isEqualTo("file/path/download.txt");
    }

    @DisplayName("Should upload object to S3")
    @Test
    void shouldUploadObject() {
        when(properties.getBucket()).thenReturn(BUCKET_NAME);
        PutObjectResponse expectedResponse = PutObjectResponse.builder().eTag("etag-123").build();
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class))).thenReturn(expectedResponse);

        RequestBody requestBody = RequestBody.fromString("test data");
        PutObjectResponse response = s3StorageService.upload("file/path/upload.txt", requestBody);

        assertThat(response).isEqualTo(expectedResponse);

        ArgumentCaptor<PutObjectRequest> captor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client).putObject(captor.capture(), eq(requestBody));
        PutObjectRequest request = captor.getValue();
        assertThat(request.bucket()).isEqualTo(BUCKET_NAME);
        assertThat(request.key()).isEqualTo("file/path/upload.txt");
    }

    @DisplayName("Should delete object in S3")
    @Test
    void shouldDeleteObject() {
        when(properties.getBucket()).thenReturn(BUCKET_NAME);

        s3StorageService.delete("file/path/delete.txt");

        ArgumentCaptor<DeleteObjectRequest> captor = ArgumentCaptor.forClass(DeleteObjectRequest.class);
        verify(s3Client).deleteObject(captor.capture());
        DeleteObjectRequest request = captor.getValue();
        assertThat(request.bucket()).isEqualTo(BUCKET_NAME);
        assertThat(request.key()).isEqualTo("file/path/delete.txt");
    }

    @DisplayName("Should create presigned upload request in S3")
    @Test
    void shouldCreatePresignedUpload() {
        when(properties.getBucket()).thenReturn(BUCKET_NAME);
        PresignedPutObjectRequest expectedPresigned = mock(PresignedPutObjectRequest.class);
        when(s3Presigner.presignPutObject(any(PutObjectPresignRequest.class))).thenReturn(expectedPresigned);

        Duration expiration = Duration.ofMinutes(15);
        PresignedPutObjectRequest result = s3StorageService.createPresignedUpload("file/path/presigned.txt", "text/plain", expiration);

        assertThat(result).isEqualTo(expectedPresigned);

        ArgumentCaptor<PutObjectPresignRequest> captor = ArgumentCaptor.forClass(PutObjectPresignRequest.class);
        verify(s3Presigner).presignPutObject(captor.capture());
        PutObjectPresignRequest request = captor.getValue();
        assertThat(request.signatureDuration()).isEqualTo(expiration);
        assertThat(request.putObjectRequest().bucket()).isEqualTo(BUCKET_NAME);
        assertThat(request.putObjectRequest().key()).isEqualTo("file/path/presigned.txt");
        assertThat(request.putObjectRequest().contentType()).isEqualTo("text/plain");
    }
}
