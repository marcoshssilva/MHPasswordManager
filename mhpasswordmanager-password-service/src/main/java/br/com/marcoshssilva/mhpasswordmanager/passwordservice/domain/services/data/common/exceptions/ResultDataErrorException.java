package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.common.exceptions;

public class ResultDataErrorException extends Exception {
    public ResultDataErrorException(String message) {
        super(message);
    }

    public ResultDataErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}
