package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.impl;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.exceptions.FailSendEmailException;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.RegisteredUserKeyVerificationMailMessage;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.SendEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SendEmailServiceImpl implements SendEmailService {
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void sendEmailRecoveryPassword(RegisteredUserKeyVerificationMailMessage message) throws FailSendEmailException {
        try {
            log.debug("Send message to {}...", message.getEmail());
            this.rabbitTemplate.convertAndSend("email.send-recovery-code", message);
        } catch (Exception e) {
            throw new FailSendEmailException(e.getMessage(), e);
        }

    }
}
