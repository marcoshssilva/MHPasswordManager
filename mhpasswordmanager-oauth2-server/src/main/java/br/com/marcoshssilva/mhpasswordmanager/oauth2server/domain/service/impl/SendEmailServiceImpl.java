package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.impl;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.exceptions.FailSendEmailException;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.AMQPDataTemplatedMailEventModel;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.AMQPMailEventModel;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.RegisteredUserCheckMailVerificationMessage;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.RegisteredUserKeyVerificationMailMessage;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.SendEmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class SendEmailServiceImpl implements SendEmailService {
    private final RabbitTemplate rabbitTemplate;

    @Value("${config.mail.queues-key.send-event:email.send-event}")
    private String sendEventMailByMessagingQueueName;

    @Override
    public void sendEmailRecoveryPassword(RegisteredUserKeyVerificationMailMessage message) throws FailSendEmailException {
        HashMap<String, Serializable> params = new HashMap<>();

        params.put("name", message.getName());
        params.put("code", message.getCode());

        sendMailByMessaging(AMQPMailEventModel.builder()
                .data(AMQPDataTemplatedMailEventModel.builder()
                        .template("confirm-recovery-code")
                        .to(message.getEmail())
                        .subject(String.format("%s, aqui está seu código de confirmação", message.getName()))
                        .params(params)
                        .build())
                .build());
    }

    @Override
    public void sendEmailVerifyAccount(RegisteredUserCheckMailVerificationMessage message) throws FailSendEmailException {
        HashMap<String, Serializable> params = new HashMap<>();

        params.put("mail", message.getEmail());
        params.put("name", message.getName());
        params.put("link", message.getLink());

        sendMailByMessaging(AMQPMailEventModel.builder()
                .data(AMQPDataTemplatedMailEventModel.builder()
                        .template("confirm-user-registration")
                        .to(message.getEmail())
                        .subject("Sua conta em PasswordManager está registrada!")
                        .params(params)
                        .build())
                .build());
    }

    public void sendMailByMessaging(AMQPMailEventModel messageData) throws FailSendEmailException {
        try {
            rabbitTemplate.convertAndSend(sendEventMailByMessagingQueueName, messageData);
        } catch (Exception e) {
            throw new FailSendEmailException(e.getMessage(), e);
        }
    }
}
