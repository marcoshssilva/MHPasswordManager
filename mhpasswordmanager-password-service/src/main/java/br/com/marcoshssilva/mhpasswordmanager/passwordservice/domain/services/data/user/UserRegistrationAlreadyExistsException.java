package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user;

public class UserRegistrationAlreadyExistsException extends Exception {
    public UserRegistrationAlreadyExistsException(String message) {
        super(message);
    }

    public UserRegistrationAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
