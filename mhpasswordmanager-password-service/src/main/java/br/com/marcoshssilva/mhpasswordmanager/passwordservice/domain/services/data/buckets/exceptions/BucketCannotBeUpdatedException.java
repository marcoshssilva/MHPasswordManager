package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.exceptions;

public class BucketCannotBeUpdatedException extends Exception {
    public BucketCannotBeUpdatedException() {
    }

    public BucketCannotBeUpdatedException(String message) {
        super(message);
    }

    public BucketCannotBeUpdatedException(String message, Throwable cause) {
        super(message, cause);
    }

    public BucketCannotBeUpdatedException(Throwable cause) {
        super(cause);
    }

    public BucketCannotBeUpdatedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
