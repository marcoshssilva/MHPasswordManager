package br.com.marcoshssilva.mhpasswordmanager.emailservice.domain.services.mail;

import br.com.marcoshssilva.mhpasswordmanager.emailservice.domain.services.html.HTMLTemplateEngineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.util.ReflectionTestUtils;

import javax.mail.internet.MimeMessage;
import java.io.Serializable;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SMTPMailMessageServiceImplTests {

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private HTMLTemplateEngineService htmlTemplateEngineService;

    private SMTPMailMessageServiceImpl service;
    private final String sender = "admin@example.com";

    @BeforeEach
    void setUp() {
        service = new SMTPMailMessageServiceImpl(javaMailSender, htmlTemplateEngineService, sender);
        ReflectionTestUtils.setField(service, "addressRedirectMail", "redirect@example.com");
        ReflectionTestUtils.setField(service, "enableRedirectMail", false);
    }

    @DisplayName("Should return expected sender and components")
    @Test
    void shouldReturnExpectedSenderAndComponents() {
        assertEquals(sender, service.getSender());
        assertSame(javaMailSender, service.getJavaMailSender());
        assertSame(htmlTemplateEngineService, service.getHTMLTemplateEngineService());
    }

    @DisplayName("Should send email using JavaMailSender")
    @Test
    void shouldSendEmail() {
        MimeMessage mimeMessage = new JavaMailSenderImpl().createMimeMessage();
        service.sendEmail(mimeMessage);

        verify(javaMailSender).send(mimeMessage);
    }

    @DisplayName("Should prepare simple mime message without redirect")
    @Test
    void shouldPrepareSimpleMimeMessageWithoutRedirect() throws Exception {
        when(javaMailSender.createMimeMessage()).thenReturn(new JavaMailSenderImpl().createMimeMessage());

        MimeMessage message = service.prepareSimpleMimeMessage("dest@example.com", "Test Subject", "Hello World", false, false);

        assertNotNull(message);
        assertEquals("Test Subject", message.getSubject());
        assertEquals("dest@example.com", message.getAllRecipients()[0].toString());
        assertEquals("admin@example.com", message.getFrom()[0].toString());
    }

    @DisplayName("Should prepare simple mime message with redirect when enabled")
    @Test
    void shouldPrepareSimpleMimeMessageWithRedirect() throws Exception {
        ReflectionTestUtils.setField(service, "enableRedirectMail", true);
        when(javaMailSender.createMimeMessage()).thenReturn(new JavaMailSenderImpl().createMimeMessage());

        MimeMessage message = service.prepareSimpleMimeMessage("dest@example.com", "Test Subject", "Hello World", true, false);

        assertNotNull(message);
        assertEquals("Test Subject", message.getSubject());
        assertEquals("redirect@example.com", message.getAllRecipients()[0].toString());
    }

    @DisplayName("Should prepare templated mime message")
    @Test
    void shouldPrepareTemplatedMimeMessage() throws Exception {
        when(javaMailSender.createMimeMessage()).thenReturn(new JavaMailSenderImpl().createMimeMessage());
        Map<String, ? extends Serializable> params = Map.of("name", "John");
        when(htmlTemplateEngineService.prepareHtmlMailMessage("template-test", params))
                .thenReturn("<html><body>Hello John</body></html>");

        MimeMessage message = service.prepareTemplatedMimeMessage("dest@example.com", "Welcome", "template-test", false, params);

        assertNotNull(message);
        assertEquals("Welcome", message.getSubject());
        assertEquals("dest@example.com", message.getAllRecipients()[0].toString());
    }
}
