package br.com.marcoshssilva.mhpasswordmanager.fileservice.amqp.models;

import lombok.Data;

@Data
public class FileEncryptionFailedEvent {
    private String fileId;
    private String error;
}
