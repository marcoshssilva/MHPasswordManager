package br.com.marcoshssilva.mhpasswordmanager.passwordservice.amqp.models;

import lombok.Data;

@Data
public class FileEncryptionCompletedEvent {
    private String fileId;
    private String encryptedObjectKey;
}
