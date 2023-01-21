package br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.domain.exceptions;

public class CannotRegisterUserException extends RuntimeException{
    public CannotRegisterUserException(String message) {
        super(message);
    }
}
