package br.com.marcoshssilva.mhpasswordmanager.fileservice.amqp.models;

import lombok.Data;

@Data
public class FileEncryptionRequestedEvent {
    private String fileId;
    private String bucketUuid;
    private String sourceObjectKey;
    private String encryptedObjectKey;
}
