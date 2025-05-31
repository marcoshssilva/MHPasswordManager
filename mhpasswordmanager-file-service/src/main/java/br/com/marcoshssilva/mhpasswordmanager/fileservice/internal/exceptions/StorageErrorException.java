package br.com.marcoshssilva.mhpasswordmanager.fileservice.internal.exceptions;

public class StorageErrorException extends Exception {
    public StorageErrorException() {
    }

    public StorageErrorException(String message) {
        super(message);
    }

    public StorageErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public StorageErrorException(Throwable cause) {
        super(cause);
    }

    public StorageErrorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
