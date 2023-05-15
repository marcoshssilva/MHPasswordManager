package br.com.marcoshssilva.mhpasswordmanager.emailservice.domain.services.mail;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

@Service
@Profile({"test"})
@Slf4j
public class FakeMailMessageServiceImpl extends AbstractMailMessageService implements MailMessageService {
    public FakeMailMessageServiceImpl(JavaMailSender javaMailSender) {
        super(javaMailSender);
    }

    @Override
    public void sendEmail(MimeMessage message) {
        log.info("Send message: {}", message);
    }
}
