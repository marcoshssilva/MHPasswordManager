package br.com.marcoshssilva.mhpasswordmanager.configservices.crypto;

public class CryptoErrorException extends Exception {
    public CryptoErrorException(String message) {
        super(message);
    }

    public CryptoErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}
