package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.exceptions;

public class UserAuthorizationCannotBeLoadedException extends Exception {
    public UserAuthorizationCannotBeLoadedException() {
    }

    public UserAuthorizationCannotBeLoadedException(String message) {
        super(message);
    }

    public UserAuthorizationCannotBeLoadedException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserAuthorizationCannotBeLoadedException(Throwable cause) {
        super(cause);
    }

    public UserAuthorizationCannotBeLoadedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
