package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.exceptions.FailSendEmailException;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.RegisteredUserKeyVerificationMailMessage;

public interface SendEmailService {
    void sendEmailRecoveryPassword(RegisteredUserKeyVerificationMailMessage message) throws FailSendEmailException;
}
