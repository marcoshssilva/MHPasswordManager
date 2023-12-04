package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.exceptions;

public class CannotRegisterUserException extends RuntimeException{
    public CannotRegisterUserException(String message) {
        super(message);
    }
}
