package br.com.marcoshssilva.mhpasswordmanager.emailservice.domain.services.mail;

import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

@Service
@ConditionalOnProperty(prefix = "application.email", name = "type", havingValue = "fake-mail", matchIfMissing = true)
@Slf4j
public class FakeMailMessageServiceImpl extends AbstractMailMessageService implements MailMessageService {
    public FakeMailMessageServiceImpl() {
        super();
    }

    @Override
    public void sendEmail(MimeMessage message) {
        log.info("Send fake message: {}", message);
    }

    @Override
    public String getSender() {
        return "no-reply@localhost.fake.mail";
    }

    @Override
    public JavaMailSender getJavaMailSender() {
        return new JavaMailSenderImpl();
    }
}
