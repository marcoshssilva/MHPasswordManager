package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.exceptions;

public class JwkLoaderFailException extends RuntimeException {
    public JwkLoaderFailException(String message, Throwable cause) {
        super(message, cause);
    }
}
