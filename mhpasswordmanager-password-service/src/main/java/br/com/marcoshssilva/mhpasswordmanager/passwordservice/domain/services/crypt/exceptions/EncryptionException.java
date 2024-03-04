package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt.exceptions;

public class EncryptionException extends RuntimeException{
    public EncryptionException(String message, Throwable cause) {
        super(message, cause);
    }
}
