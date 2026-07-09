package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.exceptions;

public class UserOperationErrorException extends RuntimeException {
    public UserOperationErrorException(String message) {
        super(message);
    }
}
