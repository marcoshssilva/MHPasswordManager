package br.com.marcoshssilva.mhpasswordmanager.emailservice.domain.services.mail;

import br.com.marcoshssilva.mhpasswordmanager.emailservice.domain.services.html.HTMLTemplateEngineService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

@Service
@ConditionalOnProperty(prefix = "application.email", name = "type", havingValue = "mail")
@lombok.extern.slf4j.Slf4j
public class SMTPMailMessageServiceImpl extends AbstractMailMessageService implements MailMessageService {
    private final String sender;
    private final JavaMailSender javaMailSender;
    private final HTMLTemplateEngineService htmlTemplateEngineService;

    public SMTPMailMessageServiceImpl(JavaMailSender javaMailSender, HTMLTemplateEngineService htmlTemplateEngineService, @Value("${application.email.default-sender}") String sender) {
        super();
        this.javaMailSender = javaMailSender;
        this.htmlTemplateEngineService = htmlTemplateEngineService;
        this.sender = sender;
    }

    @Override
    public void sendEmail(MimeMessage message) {
        this.javaMailSender.send(message);
    }

    @Override
    public String getSender() {
        return this.sender;
    }

    @Override
    public JavaMailSender getJavaMailSender() {
        return javaMailSender;
    }

    @Override
    public HTMLTemplateEngineService getHTMLTemplateEngineService() {
        return htmlTemplateEngineService;
    }
}