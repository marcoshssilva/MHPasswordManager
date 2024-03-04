package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt.exceptions;

public class DecryptionException extends RuntimeException {
    public DecryptionException(String message, Throwable cause) {
        super(message, cause);
    }
}
