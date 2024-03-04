package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.exceptions;

public class KeyRegistrationErrorException extends Exception {
    public KeyRegistrationErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public KeyRegistrationErrorException(String message) {
        super(message);
    }
}
