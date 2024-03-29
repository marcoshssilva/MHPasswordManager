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

    @Value("${application.email.address-redirect-mail}")
    protected String addressRedirectMail;

    @Value(("${application.email.enable-redirect-mail}"))
    protected Boolean enableRedirectMail;

    public abstract String getSender();
    public abstract JavaMailSender getJavaMailSender();

    @Override
    public MimeMessage prepareMimeMessage(String destination, String subject, String body, Boolean isHtml, Boolean isMultipart) throws MessagingException {
        MimeMessage mm = getJavaMailSender().createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mm, isMultipart);

        helper.setFrom(getSender());
        helper.setSubject(subject);
        helper.setSentDate(new Date());
        helper.setText(body, isHtml);

        if (Boolean.TRUE.equals(enableRedirectMail)) {
            helper.setTo(addressRedirectMail);
        } else {
            helper.setTo(destination);
        }

        return mm;
    }

}
