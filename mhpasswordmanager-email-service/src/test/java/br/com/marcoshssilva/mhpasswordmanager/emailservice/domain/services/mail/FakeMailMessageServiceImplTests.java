package br.com.marcoshssilva.mhpasswordmanager.emailservice.domain.services.mail;

import br.com.marcoshssilva.mhpasswordmanager.emailservice.domain.services.html.HTMLTemplateEngineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.mail.internet.MimeMessage;
import java.io.Serializable;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FakeMailMessageServiceImplTests {

    @Mock
    private HTMLTemplateEngineService htmlTemplateEngineService;

    private FakeMailMessageServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new FakeMailMessageServiceImpl(htmlTemplateEngineService);
        ReflectionTestUtils.setField(service, "addressRedirectMail", "redirect@localhost.fake.mail");
        ReflectionTestUtils.setField(service, "enableRedirectMail", false);
    }

    @DisplayName("Should return expected sender and components")
    @Test
    void shouldReturnExpectedSenderAndComponents() {
        assertEquals("no-reply@localhost.fake.mail", service.getSender());
        assertNotNull(service.getJavaMailSender());
        assertSame(htmlTemplateEngineService, service.getHTMLTemplateEngineService());
    }

    @DisplayName("Should send email without exceptions (fake log)")
    @Test
    void shouldSendEmail() throws Exception {
        MimeMessage mimeMessage = service.prepareSimpleMimeMessage("recipient@example.com", "Subject", "Body", false, false);
        service.sendEmail(mimeMessage);
    }

    @DisplayName("Should prepare simple mime message without redirect")
    @Test
    void shouldPrepareSimpleMimeMessageWithoutRedirect() throws Exception {
        MimeMessage message = service.prepareSimpleMimeMessage("dest@example.com", "Test Subject", "Hello World", false, false);

        assertNotNull(message);
        assertEquals("Test Subject", message.getSubject());
        assertEquals("dest@example.com", message.getAllRecipients()[0].toString());
        assertEquals("no-reply@localhost.fake.mail", message.getFrom()[0].toString());
    }

    @DisplayName("Should prepare simple mime message with redirect when enabled")
    @Test
    void shouldPrepareSimpleMimeMessageWithRedirect() throws Exception {
        ReflectionTestUtils.setField(service, "enableRedirectMail", true);

        MimeMessage message = service.prepareSimpleMimeMessage("dest@example.com", "Test Subject", "Hello World", true, false);

        assertNotNull(message);
        assertEquals("Test Subject", message.getSubject());
        assertEquals("redirect@localhost.fake.mail", message.getAllRecipients()[0].toString());
    }

    @DisplayName("Should prepare templated mime message")
    @Test
    void shouldPrepareTemplatedMimeMessage() throws Exception {
        Map<String, ? extends Serializable> params = Map.of("name", "John");
        when(htmlTemplateEngineService.prepareHtmlMailMessage("template-test", params))
                .thenReturn("<html><body>Hello John</body></html>");

        MimeMessage message = service.prepareTemplatedMimeMessage("dest@example.com", "Welcome", "template-test", false, params);

        assertNotNull(message);
        assertEquals("Welcome", message.getSubject());
        assertEquals("dest@example.com", message.getAllRecipients()[0].toString());
    }
}
