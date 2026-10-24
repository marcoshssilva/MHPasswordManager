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
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

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
        Duration expiration = Duration.ofMinutes(15);
        String objectKey = "file/path/presigned.txt";
        String mimeType = "text/plain";
        int daysToAdd = 1;

        PresignedPutObjectRequest expectedPresigned = PresignedPutObjectRequest.builder()
                .expiration(LocalDate.now(ZoneId.systemDefault()).plusDays(daysToAdd).atStartOfDay(ZoneId.systemDefault()).toInstant())
                .isBrowserExecutable(Boolean.FALSE)
                .signedHeaders(
                        java.util.Collections.singletonMap("Content-Type", List.of(mimeType))
                )
                .httpRequest(
                        software.amazon.awssdk.http.SdkHttpRequest.builder()
                                .method(software.amazon.awssdk.http.SdkHttpMethod.PUT)
                                .uri(URI.create("https://s3.amazonaws.com/" + BUCKET_NAME + "/" + objectKey))
                                .putHeader("Content-Type", mimeType)
                                .build()
                )
                .build();

        when(properties.getBucket()).thenReturn(BUCKET_NAME);
        doReturn(expectedPresigned).when(s3Presigner).presignPutObject(any(java.util.function.Consumer.class));

        PresignedPutObjectRequest result = s3StorageService.createPresignedUpload(objectKey, mimeType, expiration);

        assertThat(result).isEqualTo(expectedPresigned);

        @SuppressWarnings("unchecked") ArgumentCaptor<java.util.function.Consumer<PutObjectPresignRequest.Builder>> captor = ArgumentCaptor.forClass(java.util.function.Consumer.class);

        verify(s3Presigner).presignPutObject(captor.capture());

        PutObjectPresignRequest.Builder builder = PutObjectPresignRequest.builder();
        captor.getValue().accept(builder);
        PutObjectPresignRequest request = builder.build();

        assertThat(request.signatureDuration()).isEqualTo(expiration);
        assertThat(request.putObjectRequest().bucket()).isEqualTo(BUCKET_NAME);
        assertThat(request.putObjectRequest().key()).isEqualTo(objectKey);
        assertThat(request.putObjectRequest().contentType()).isEqualTo(mimeType);
    }
}
