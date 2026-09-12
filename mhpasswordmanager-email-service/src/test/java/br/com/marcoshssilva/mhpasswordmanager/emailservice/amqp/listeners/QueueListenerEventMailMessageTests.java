package br.com.marcoshssilva.mhpasswordmanager.emailservice.amqp.listeners;

import br.com.marcoshssilva.mhpasswordmanager.emailservice.amqp.models.AMQPDataEventModel;
import br.com.marcoshssilva.mhpasswordmanager.emailservice.amqp.models.AMQPDataSimpleMailEventModel;
import br.com.marcoshssilva.mhpasswordmanager.emailservice.amqp.models.AMQPDataTemplatedMailEventModel;
import br.com.marcoshssilva.mhpasswordmanager.emailservice.amqp.models.AMQPEventModel;
import br.com.marcoshssilva.mhpasswordmanager.emailservice.domain.services.mail.MailMessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QueueListenerEventMailMessageTests {

    @Mock
    private MailMessageService mailMessageService;

    private ObjectMapper objectMapper;
    private QueueListenerEventMailMessage listener;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        listener = new QueueListenerEventMailMessage(objectMapper, mailMessageService);
    }

    @DisplayName("Should process and send simple email message from queue event")
    @Test
    void shouldProcessSimpleEmailMessage() throws Exception {
        AMQPDataSimpleMailEventModel data = AMQPDataSimpleMailEventModel.builder()
                .to("dest@example.com")
                .subject("Test Simple Subject")
                .body("Hello Simple Email")
                .isHtml(Boolean.FALSE)
                .build();

        AMQPEventModel event = new AMQPEventModel("event-1", List.of("mail"), data);
        byte[] payload = objectMapper.writeValueAsBytes(event);
        Message message = new Message(payload, new MessageProperties());

        MimeMessage mimeMessage = new JavaMailSenderImpl().createMimeMessage();
        when(mailMessageService.prepareSimpleMimeMessage("dest@example.com", "Test Simple Subject", "Hello Simple Email", false, false))
                .thenReturn(mimeMessage);

        listener.onReceiveMessage(message);

        verify(mailMessageService).prepareSimpleMimeMessage("dest@example.com", "Test Simple Subject", "Hello Simple Email", false, false);
        verify(mailMessageService).sendEmail(mimeMessage);
    }

    @DisplayName("Should process and send templated email message from queue event")
    @Test
    void shouldProcessTemplatedEmailMessage() throws Exception {
        AMQPDataTemplatedMailEventModel data = AMQPDataTemplatedMailEventModel.builder()
                .template("confirm-user-registration")
                .params(Map.of("name", "John", "link", "http://localhost/verify"))
                .build();
        data.setTo("dest@example.com");
        data.setSubject("Welcome Registration");

        AMQPEventModel event = new AMQPEventModel("event-2", List.of("mail"), data);
        byte[] payload = objectMapper.writeValueAsBytes(event);
        Message message = new Message(payload, new MessageProperties());

        MimeMessage mimeMessage = new JavaMailSenderImpl().createMimeMessage();
        when(mailMessageService.prepareTemplatedMimeMessage(eq("dest@example.com"), eq("Welcome Registration"), eq("confirm-user-registration"), eq(false), any()))
                .thenReturn(mimeMessage);

        listener.onReceiveMessage(message);

        verify(mailMessageService).prepareTemplatedMimeMessage(eq("dest@example.com"), eq("Welcome Registration"), eq("confirm-user-registration"), eq(false), any());
        verify(mailMessageService).sendEmail(mimeMessage);
    }

    @DisplayName("Should throw AmqpRejectAndDontRequeueException when unknown data model is received")
    @Test
    void shouldThrowWhenUnknownDataModelReceived() {
        AMQPEventModel event = new AMQPEventModel("event-3", List.of("mail"), new CustomUnknownDataModel());
        Message message = new Message(new byte[0], new MessageProperties());

        assertThrows(AmqpRejectAndDontRequeueException.class, () -> listener.onReceiveMessage(message));
    }

    @DisplayName("Should throw AmqpRejectAndDontRequeueException when payload is invalid JSON")
    @Test
    void shouldThrowWhenPayloadIsInvalid() {
        Message message = new Message("invalid json".getBytes(), new MessageProperties());

        assertThrows(AmqpRejectAndDontRequeueException.class, () -> listener.onReceiveMessage(message));
    }

    @DisplayName("Should throw AmqpRejectAndDontRequeueException when mailMessageService throws exception")
    @Test
    void shouldThrowWhenServiceThrows() throws Exception {
        AMQPDataSimpleMailEventModel data = AMQPDataSimpleMailEventModel.builder()
                .to("dest@example.com")
                .subject("Test Subject")
                .body("Hello Email")
                .isHtml(Boolean.FALSE)
                .build();

        AMQPEventModel event = new AMQPEventModel("event-4", List.of("mail"), data);
        byte[] payload = objectMapper.writeValueAsBytes(event);
        Message message = new Message(payload, new MessageProperties());

        when(mailMessageService.prepareSimpleMimeMessage("dest@example.com", "Test Subject", "Hello Email", false, false))
                .thenThrow(new RuntimeException("SMTP Server Down"));

        assertThrows(AmqpRejectAndDontRequeueException.class, () -> listener.onReceiveMessage(message));
    }

    static class CustomUnknownDataModel extends AMQPDataEventModel {
    }
}
