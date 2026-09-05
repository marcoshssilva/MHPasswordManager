package br.com.marcoshssilva.mhpasswordmanager.passwordservice.amqp.models;

import lombok.Data;

@Data
public class FileEncryptionRequestedEvent {
    private String fileId;
    private String bucketUuid;
    private String sourceObjectKey;
    private String encryptedObjectKey;
}
