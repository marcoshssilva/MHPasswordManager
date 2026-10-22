package br.com.marcoshssilva.mhpasswordmanager.fileservice.amqp.exceptions;

public class MustAbortMessageQueueException extends RuntimeException {
    public MustAbortMessageQueueException(String message) {
        super(message);
    }
}
