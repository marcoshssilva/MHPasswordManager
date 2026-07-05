package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.exceptions;

public class BusinessRuleException extends Exception {
    public BusinessRuleException(String message) {
        super(message);
    }

    public BusinessRuleException(String message, Exception e) {
        super(message, e);
    }
}
