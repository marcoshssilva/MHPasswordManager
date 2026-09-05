package br.com.marcoshssilva.mhpasswordmanager.passwordservice.amqp.models;

import lombok.Data;

@Data
public class FileEncryptionFailedEvent {
    private String fileId;
    private String error;
}
