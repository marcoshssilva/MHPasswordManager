package br.com.marcoshssilva.mhpasswordmanager.apigateway.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class S3StorageConfigTest {

    @DisplayName("Must build S3Client and resolve")
    @Test
    void buildsClientForAwsAndCompatibleEndpointWithoutResolvingCredentials() {
        var properties = new S3StorageProperties("http://localhost:9001", "us-east-1", "test", "fake", "fakeS3cr3t", true);
        var config = new S3StorageConfig();

        try (var client = config.avatarS3Client(properties)) {
            assertThat(client.serviceName()).isEqualTo("s3");
        }
        properties.setEndpoint("http://localhost:9000");
        properties.setPathStyleAccess(true);

        try (var client = config.avatarS3Client(properties)) {
            assertThat(client.serviceName()).isEqualTo("s3");
        }
    }

}
