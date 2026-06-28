package br.com.marcoshssilva.mhpasswordmanager.emailservice.domain.services.mail;

import br.com.marcoshssilva.mhpasswordmanager.emailservice.domain.services.html.HTMLTemplateEngineService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@RequiredArgsConstructor
public abstract class AbstractMailMessageService implements MailMessageService {

    @Value("${application.email.address-redirect-mail}")
    protected String addressRedirectMail;

    @Value(("${application.email.enable-redirect-mail}"))
    protected Boolean enableRedirectMail;

    public abstract String getSender();
    public abstract JavaMailSender getJavaMailSender();
    public abstract HTMLTemplateEngineService getHTMLTemplateEngineService();

    @Override
    @SuppressWarnings("java:S2143")
    public MimeMessage prepareMimeMessage(String destination, String subject, String body, Boolean isHtml, Boolean isMultipart) throws MessagingException {
        MimeMessage mm = getJavaMailSender().createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mm, isMultipart);

        helper.setFrom(getSender());
        helper.setSubject(subject);
        helper.setSentDate(Date.from(Instant.now(Clock.systemUTC())));
        helper.setText(body, isHtml);

        if (Boolean.TRUE.equals(enableRedirectMail)) {
            helper.setTo(addressRedirectMail);
        } else {
            helper.setTo(destination);
        }

        return mm;
    }

    @Override
    public MimeMessage prepareTemplatedMimeMessage(String destination, String subject, String templateName, Boolean isMultipart, Map<String, Object> params) throws MessagingException {
        String mailMessage = getHTMLTemplateEngineService().prepareHtmlMailMessage("confirm-recovery-code", params);
        return prepareMimeMessage(destination, subject, mailMessage, Boolean.TRUE, isMultipart);
    }

}
