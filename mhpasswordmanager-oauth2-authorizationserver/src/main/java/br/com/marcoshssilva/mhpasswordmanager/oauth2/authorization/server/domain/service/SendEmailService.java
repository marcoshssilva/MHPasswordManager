package br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.domain.service;

import br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.domain.exceptions.FailSendEmailException;
import br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.domain.models.RegisteredUserKeyVerificationMailMessage;

public interface SendEmailService {
    void sendEmailRecoveryPassword(RegisteredUserKeyVerificationMailMessage message) throws FailSendEmailException;
}
