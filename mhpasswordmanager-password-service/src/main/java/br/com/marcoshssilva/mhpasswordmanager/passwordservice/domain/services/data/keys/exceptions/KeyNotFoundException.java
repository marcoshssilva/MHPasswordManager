package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.exceptions;

public class KeyNotFoundException extends Exception {
    public KeyNotFoundException(String message) {
        super(message);
    }

    public KeyNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
