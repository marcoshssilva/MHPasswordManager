package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.exceptions;

public class KeyEncodedErrorConverterException extends Exception {
    public KeyEncodedErrorConverterException() {
        super();
    }

    public KeyEncodedErrorConverterException(String message) {
        super(message);
    }

    public KeyEncodedErrorConverterException(String message, Throwable cause) {
        super(message, cause);
    }

    public KeyEncodedErrorConverterException(Throwable cause) {
        super(cause);
    }

    protected KeyEncodedErrorConverterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
