package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.exceptions;

public class BucketNotFoundException extends Exception {
    public BucketNotFoundException() {
        super();
    }

    public BucketNotFoundException(String message) {
        super(message);
    }

    public BucketNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public BucketNotFoundException(Throwable cause) {
        super(cause);
    }

    protected BucketNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
