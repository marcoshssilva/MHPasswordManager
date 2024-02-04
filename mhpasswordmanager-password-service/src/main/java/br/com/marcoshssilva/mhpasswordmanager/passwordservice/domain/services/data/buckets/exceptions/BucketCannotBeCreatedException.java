package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.exceptions;

public class BucketCannotBeCreatedException extends Exception {
    public BucketCannotBeCreatedException() {
        super();
    }

    public BucketCannotBeCreatedException(String message) {
        super(message);
    }

    public BucketCannotBeCreatedException(String message, Throwable cause) {
        super(message, cause);
    }

    public BucketCannotBeCreatedException(Throwable cause) {
        super(cause);
    }

    protected BucketCannotBeCreatedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
