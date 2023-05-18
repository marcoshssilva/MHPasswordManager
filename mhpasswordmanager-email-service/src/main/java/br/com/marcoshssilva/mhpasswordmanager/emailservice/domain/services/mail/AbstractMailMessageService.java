package br.com.marcoshssilva.mhpasswordmanager.emailservice.domain.services.mail;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Date;

@RequiredArgsConstructor
public abstract class AbstractMailMessageService implements MailMessageService {

    @Value("${spring.mail.username}")
    protected String sender;

    protected final JavaMailSender javaMailSender;

    @Override
    public MimeMessage prepareMimeMessage(String destination, String subject, String body, Boolean isHtml, Boolean isMultipart) throws MessagingException {
        MimeMessage mm = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mm, isMultipart);

        helper.setTo(destination);
        helper.setFrom(sender);
        helper.setSubject(subject);
        helper.setSentDate(new Date());
        helper.setText(body, isHtml);

        return mm;
    }

}
