package br.com.marcoshssilva.mhpasswordmanager.fileservice.internal.impl;

import br.com.marcoshssilva.mhpasswordmanager.fileservice.configs.S3StorageProperties;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.internal.IS3StorageService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
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

import java.time.Duration;

@Service
@ConditionalOnProperty(prefix = "storage.s3", name = "endpoint")
public class S3StorageServiceImpl implements IS3StorageService {
    private final S3StorageProperties properties;
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    public S3StorageServiceImpl(S3StorageProperties properties, S3Client s3Client, S3Presigner s3Presigner) {
        this.properties = properties;
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
    }

    @Override
    public String getBucketName() {
        return properties.getBucket();
    }

    @Override
    public HeadObjectResponse getObjectMetadata(String objectKey) {
        return s3Client.headObject(HeadObjectRequest.builder().bucket(getBucketName()).key(objectKey).build());
    }

    @Override
    public ResponseInputStream<GetObjectResponse> download(String objectKey) {
        return s3Client.getObject(GetObjectRequest.builder().bucket(getBucketName()).key(objectKey).build());
    }

    @Override
    public PutObjectResponse upload(String objectKey, RequestBody requestBody) {
        return s3Client.putObject(PutObjectRequest.builder().bucket(getBucketName()).key(objectKey).build(), requestBody);
    }

    @Override
    public PresignedPutObjectRequest createPresignedUpload(String objectKey, String contentType, Duration expiration) {
        PutObjectRequest request = PutObjectRequest.builder().bucket(getBucketName()).key(objectKey).contentType(contentType).build();
        return s3Presigner.presignPutObject(PutObjectPresignRequest.builder().signatureDuration(expiration).putObjectRequest(request).build());
    }
}
