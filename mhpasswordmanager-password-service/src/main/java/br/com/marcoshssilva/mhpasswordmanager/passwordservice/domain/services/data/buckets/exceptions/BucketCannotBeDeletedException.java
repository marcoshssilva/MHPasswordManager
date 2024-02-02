package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.exceptions;

public class BucketCannotBeDeletedException extends Exception {
    public BucketCannotBeDeletedException() {
    }

    public BucketCannotBeDeletedException(String message) {
        super(message);
    }

    public BucketCannotBeDeletedException(String message, Throwable cause) {
        super(message, cause);
    }

    public BucketCannotBeDeletedException(Throwable cause) {
        super(cause);
    }

    public BucketCannotBeDeletedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
