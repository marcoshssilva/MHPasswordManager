package br.com.marcoshssilva.mhpasswordmanager.emailservice.amqp.listeners;

import br.com.marcoshssilva.mhpasswordmanager.emailservice.amqp.models.AMQPDataSimpleMailEventModel;
import br.com.marcoshssilva.mhpasswordmanager.emailservice.amqp.models.AMQPDataTemplatedMailEventModel;
import br.com.marcoshssilva.mhpasswordmanager.emailservice.amqp.models.AMQPEventModel;
import br.com.marcoshssilva.mhpasswordmanager.emailservice.domain.services.mail.MailMessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;

@Component
@RequiredArgsConstructor
@Slf4j
public class QueueListenerEventMailMessage {
    private final ObjectMapper objectMapper;
    private final MailMessageService mailMessageService;

    @RabbitListener(queues = "${config.mail.queues-key.send-event}")
    public void onReceiveMessage(@Payload Message message) {
        try {
            AMQPEventModel amqpEventModel = objectMapper.readValue(message.getBody(), AMQPEventModel.class);
            MimeMessage mimeMessage;
            if (amqpEventModel.getData() instanceof AMQPDataSimpleMailEventModel e) {
                mimeMessage = mailMessageService.prepareMimeMessage(e.getTo(), e.getSubject(), e.getBody(), e.getIsHtml(), Boolean.FALSE);
            } else if (amqpEventModel.getData() instanceof AMQPDataTemplatedMailEventModel e) {
                mimeMessage = mailMessageService.prepareTemplatedMimeMessage(e.getTo(), e.getSubject(), e.getTemplate(), Boolean.FALSE, e.getParams());
            } else {
                throw new IllegalArgumentException("Unknown type of AMQPEventModel");
            }
            mailMessageService.sendEmail(mimeMessage);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException(e.getMessage(), Boolean.TRUE, e);
        }
    }
}
