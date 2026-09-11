package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.storage.impl;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.configs.S3StorageProperties;
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
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3StorageServiceImplTests {

    @Mock
    private S3Client s3Client;

    @Mock
    private S3Presigner s3Presigner;

    private S3StorageProperties properties;
    private S3StorageServiceImpl service;

    @BeforeEach
    void setUp() {
        properties = new S3StorageProperties();
        properties.setBucket("test-bucket");
        service = new S3StorageServiceImpl(properties, s3Client, s3Presigner);
    }

    @DisplayName("Should return bucket name from properties")
    @Test
    void shouldReturnBucketName() {
        assertThat(service.getBucketName()).isEqualTo("test-bucket");
    }

    @DisplayName("Should fetch object metadata via headObject")
    @Test
    void shouldFetchObjectMetadata() {
        HeadObjectResponse response = HeadObjectResponse.builder().contentLength(1024L).build();
        when(s3Client.headObject(any(HeadObjectRequest.class))).thenReturn(response);

        HeadObjectResponse result = service.getObjectMetadata("file-key");

        assertThat(result).isSameAs(response);
        ArgumentCaptor<HeadObjectRequest> captor = ArgumentCaptor.forClass(HeadObjectRequest.class);
        verify(s3Client).headObject(captor.capture());
        assertThat(captor.getValue().bucket()).isEqualTo("test-bucket");
        assertThat(captor.getValue().key()).isEqualTo("file-key");
    }

    @DisplayName("Should download object from S3")
    @Test
    void shouldDownloadObject() {
        GetObjectResponse response = GetObjectResponse.builder().build();
        ResponseInputStream<GetObjectResponse> responseStream = new ResponseInputStream<>(response, new ByteArrayInputStream("data".getBytes()));
        when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(responseStream);

        ResponseInputStream<GetObjectResponse> result = service.download("file-key");

        assertThat(result).isSameAs(responseStream);
        ArgumentCaptor<GetObjectRequest> captor = ArgumentCaptor.forClass(GetObjectRequest.class);
        verify(s3Client).getObject(captor.capture());
        assertThat(captor.getValue().bucket()).isEqualTo("test-bucket");
        assertThat(captor.getValue().key()).isEqualTo("file-key");
    }

    @DisplayName("Should upload object to S3")
    @Test
    void shouldUploadObject() {
        PutObjectResponse response = PutObjectResponse.builder().eTag("etag-123").build();
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class))).thenReturn(response);

        RequestBody body = RequestBody.fromString("content");
        PutObjectResponse result = service.upload("file-key", body);

        assertThat(result).isSameAs(response);
        ArgumentCaptor<PutObjectRequest> captor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client).putObject(captor.capture(), eq(body));
        assertThat(captor.getValue().bucket()).isEqualTo("test-bucket");
        assertThat(captor.getValue().key()).isEqualTo("file-key");
    }

    @DisplayName("Should create presigned upload request")
    @Test
    void shouldCreatePresignedUpload() {
        PresignedPutObjectRequest presignedResponse = mock(PresignedPutObjectRequest.class);
        when(s3Presigner.presignPutObject(any(PutObjectPresignRequest.class))).thenReturn(presignedResponse);

        PresignedPutObjectRequest result = service.createPresignedUpload("file-key", "text/plain", Duration.ofMinutes(10));

        assertThat(result).isSameAs(presignedResponse);
        ArgumentCaptor<PutObjectPresignRequest> captor = ArgumentCaptor.forClass(PutObjectPresignRequest.class);
        verify(s3Presigner).presignPutObject(captor.capture());
        assertThat(captor.getValue().signatureDuration()).isEqualTo(Duration.ofMinutes(10));
        assertThat(captor.getValue().putObjectRequest().bucket()).isEqualTo("test-bucket");
        assertThat(captor.getValue().putObjectRequest().key()).isEqualTo("file-key");
        assertThat(captor.getValue().putObjectRequest().contentType()).isEqualTo("text/plain");
    }
}
