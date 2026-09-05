package br.com.marcoshssilva.mhpasswordmanager.fileservice.amqp.models;

import lombok.Data;

@Data
public class FileEncryptionCompletedEvent {
    private String fileId;
    private String encryptedObjectKey;
}
