package br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.domain.exceptions;

public class FailSendEmailException extends Exception {
    public FailSendEmailException(String message) {
        super(message);
    }

    public FailSendEmailException(String message, Throwable cause) {
        super(message, cause);
    }
}
