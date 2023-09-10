package br.com.marcoshssilva.mhpasswordmanager.emailservice.domain.services.mail;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

@Service
@ConditionalOnProperty(prefix = "application.email", name = "type", havingValue = "mail")
@Slf4j
public class SMTPMailMessageServiceImpl extends AbstractMailMessageService implements MailMessageService {

    @Value("${application.email.default-sender}")
    protected String sender;

    protected JavaMailSender javaMailSender;
    public SMTPMailMessageServiceImpl(JavaMailSender javaMailSender) {
        super();
        this.javaMailSender = javaMailSender;
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
}
