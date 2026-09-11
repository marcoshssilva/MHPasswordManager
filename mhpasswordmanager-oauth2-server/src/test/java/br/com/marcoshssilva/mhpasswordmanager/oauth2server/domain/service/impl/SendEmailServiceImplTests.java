package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.impl;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.exceptions.FailSendEmailException;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.AMQPDataTemplatedMailEventModel;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.AMQPMailEventModel;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.RegisteredUserCheckMailVerificationMessage;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.RegisteredUserKeyVerificationMailMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SendEmailServiceImplTests {

    @Mock
    private RabbitTemplate rabbitTemplate;

    private SendEmailServiceImpl sendEmailService;

    @BeforeEach
    void setUp() {
        sendEmailService = new SendEmailServiceImpl(rabbitTemplate);
        ReflectionTestUtils.setField(sendEmailService, "sendEventMailByMessagingQueueName", "email.send-event");
    }

    @DisplayName("Should send email recovery password successfully")
    @Test
    void shouldSendEmailRecoveryPasswordSuccessfully() throws Exception {
        RegisteredUserKeyVerificationMailMessage message = RegisteredUserKeyVerificationMailMessage.builder()
                .name("John")
                .email("john@example.com")
                .code("12345678901")
                .build();

        doNothing().when(rabbitTemplate).convertAndSend(eq("email.send-event"), any(AMQPMailEventModel.class));

        assertDoesNotThrow(() -> sendEmailService.sendEmailRecoveryPassword(message));

        ArgumentCaptor<AMQPMailEventModel> captor = ArgumentCaptor.forClass(AMQPMailEventModel.class);
        verify(rabbitTemplate).convertAndSend(eq("email.send-event"), captor.capture());

        AMQPMailEventModel captured = captor.getValue();
        assertNotNull(captured.getData());
        AMQPDataTemplatedMailEventModel data = (AMQPDataTemplatedMailEventModel) captured.getData();
        assertEquals("john@example.com", data.getTo());
        assertEquals("confirm-recovery-code", data.getTemplate());
        assertEquals("John, aqui está seu código de confirmação", data.getSubject());
        assertEquals("John", data.getParams().get("name"));
        assertEquals("12345678901", data.getParams().get("code"));
    }

    @DisplayName("Should send email verify account successfully")
    @Test
    void shouldSendEmailVerifyAccountSuccessfully() throws Exception {
        RegisteredUserCheckMailVerificationMessage message = RegisteredUserCheckMailVerificationMessage.builder()
                .name("Alice")
                .email("alice@example.com")
                .link("http://localhost:12010/verify/uuid-123")
                .build();

        doNothing().when(rabbitTemplate).convertAndSend(eq("email.send-event"), any(AMQPMailEventModel.class));

        assertDoesNotThrow(() -> sendEmailService.sendEmailVerifyAccount(message));

        ArgumentCaptor<AMQPMailEventModel> captor = ArgumentCaptor.forClass(AMQPMailEventModel.class);
        verify(rabbitTemplate).convertAndSend(eq("email.send-event"), captor.capture());

        AMQPMailEventModel captured = captor.getValue();
        assertNotNull(captured.getData());
        AMQPDataTemplatedMailEventModel data = (AMQPDataTemplatedMailEventModel) captured.getData();
        assertEquals("alice@example.com", data.getTo());
        assertEquals("confirm-user-registration", data.getTemplate());
        assertEquals("Sua conta em PasswordManager está registrada!", data.getSubject());
        assertEquals("Alice", data.getParams().get("name"));
        assertEquals("alice@example.com", data.getParams().get("mail"));
        assertEquals("http://localhost:12010/verify/uuid-123", data.getParams().get("link"));
    }

    @DisplayName("Should throw FailSendEmailException when rabbitTemplate throws exception")
    @Test
    void shouldThrowFailSendEmailExceptionWhenAmqpFails() {
        RegisteredUserKeyVerificationMailMessage message = RegisteredUserKeyVerificationMailMessage.builder()
                .name("John")
                .email("john@example.com")
                .code("12345678901")
                .build();

        doThrow(new AmqpException("Broker unavailable"))
                .when(rabbitTemplate).convertAndSend(eq("email.send-event"), any(AMQPMailEventModel.class));

        assertThrows(FailSendEmailException.class, () -> sendEmailService.sendEmailRecoveryPassword(message));
    }
}
