package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.exceptions;

public class UserRegistrationDeniedAccessException extends Exception {
    public UserRegistrationDeniedAccessException() {
        super();
    }

    public UserRegistrationDeniedAccessException(String message) {
        super(message);
    }

    public UserRegistrationDeniedAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserRegistrationDeniedAccessException(Throwable cause) {
        super(cause);
    }

    protected UserRegistrationDeniedAccessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
