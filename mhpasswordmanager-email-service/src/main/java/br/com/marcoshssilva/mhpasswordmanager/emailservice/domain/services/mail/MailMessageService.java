package br.com.marcoshssilva.mhpasswordmanager.emailservice.domain.services.mail;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Map;

public interface MailMessageService {
    void sendEmail(MimeMessage message);
    MimeMessage prepareMimeMessage(String destination, String subject, String body, Boolean isHtml, Boolean isMultipart) throws MessagingException;
    MimeMessage prepareTemplatedMimeMessage(String destination, String subject, String templateName, Boolean isMultipart, Map<String, Object> params) throws MessagingException;
}
