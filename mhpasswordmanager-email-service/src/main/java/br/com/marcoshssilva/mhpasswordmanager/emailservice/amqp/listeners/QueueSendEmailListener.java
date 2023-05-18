package br.com.marcoshssilva.mhpasswordmanager.emailservice.amqp.listeners;

import br.com.marcoshssilva.mhpasswordmanager.emailservice.domain.models.SimpleMailMessage;
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

@Slf4j
@RequiredArgsConstructor
@Component
public class QueueSendEmailListener {
    private final MailMessageService mailMessageService;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = {"email.send-simple-email"})
    public void onReceiveMessage(@Payload Message message) {
        try {
            String content = new String(message.getBody(), StandardCharsets.UTF_8);
            log.debug("Received message: {}", content);

            SimpleMailMessage simpleMailMessage = objectMapper.readValue(message.getBody(), SimpleMailMessage.class);
            MimeMessage mimeMessage = mailMessageService.prepareMimeMessage(simpleMailMessage.getDestination(), simpleMailMessage.getSubject(), simpleMailMessage.getMessage(), false, false);
            mailMessageService.sendEmail(mimeMessage);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }

}
