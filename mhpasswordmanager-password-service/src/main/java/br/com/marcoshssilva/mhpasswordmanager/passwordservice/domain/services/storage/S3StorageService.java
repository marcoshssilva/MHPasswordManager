package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.storage;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.time.Duration;

public interface S3StorageService {
    String getBucketName();

    HeadObjectResponse getObjectMetadata(String objectKey);

    ResponseInputStream<GetObjectResponse> download(String objectKey);

    PutObjectResponse upload(String objectKey, RequestBody requestBody);

    PresignedPutObjectRequest createPresignedUpload(String objectKey, String contentType, Duration expiration);
}
