package br.com.marcoshssilva.mhpasswordmanager.emailservice.amqp.listeners;

import br.com.marcoshssilva.mhpasswordmanager.emailservice.domain.models.RegisteredUserMailMessage;
import br.com.marcoshssilva.mhpasswordmanager.emailservice.domain.services.html.HTMLTemplateEngineService;
import br.com.marcoshssilva.mhpasswordmanager.emailservice.domain.services.mail.MailMessageService;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class QueueSendConfirmationRegisteredUserListener {
    public static final String DEFAULT_TITLE = "Sua conta em PasswordManager está registrada!";

    private final MailMessageService mailMessageService;
    private final HTMLTemplateEngineService htmlTemplateEngineService;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = {"email.send-confirmation-registered-user"})
    public void onReceiveMessage(@Payload Message message) {
        try {
            String content = new String(message.getBody(), StandardCharsets.UTF_8);
            log.debug("Received message: {}", content);

            RegisteredUserMailMessage registeredUserMailMessage = objectMapper.readValue(message.getBody(), RegisteredUserMailMessage.class);
            HashMap<String, Object> map = new HashMap<>();
            map.put("registration", registeredUserMailMessage);
            String mailMessage = htmlTemplateEngineService.prepareHtmlMailMessage("confirm-user-registration", map);
            MimeMessage mimeMessage = mailMessageService.prepareMimeMessage(registeredUserMailMessage.getEmail(), DEFAULT_TITLE, mailMessage, true, false);

            mailMessageService.sendEmail(mimeMessage);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }
}
