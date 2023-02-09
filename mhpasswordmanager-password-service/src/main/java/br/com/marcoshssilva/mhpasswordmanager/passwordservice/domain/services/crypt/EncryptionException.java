package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt;

public class EncryptionException extends RuntimeException{
    public EncryptionException(String message, Throwable cause) {
        super(message, cause);
    }
}
