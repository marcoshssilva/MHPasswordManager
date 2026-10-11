package br.com.marcoshssilva.mhpasswordmanager.userservice.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "storage.s3")
public class S3StorageProperties {
    private String endpoint;
    private String region = "us-east-1";
    private String bucket;
    private String accessKey;
    private String secretKey;
    private boolean pathStyleAccess = true;

    public S3StorageProperties() { }

    public S3StorageProperties(String endpoint, String region, String bucket, String accessKey, String secretKey, boolean pathStyleAccess) {
        this.endpoint = endpoint;
        this.region = region;
        this.bucket = bucket;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.pathStyleAccess = pathStyleAccess;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public boolean isPathStyleAccess() {
        return pathStyleAccess;
    }

    public void setPathStyleAccess(boolean pathStyleAccess) {
        this.pathStyleAccess = pathStyleAccess;
    }

}
