package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.exceptions;

public class KeyEncodedErrorConverterException extends Exception {

    public KeyEncodedErrorConverterException(String message) {
        super(message);
    }

    public KeyEncodedErrorConverterException(String message, Throwable cause) {
        super(message, cause);
    }
}
