package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user;

public class UserRegistrationException extends Exception {
    public UserRegistrationException(String message) {
        super(message);
    }

    public UserRegistrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
