package br.com.marcoshssilva.mhpasswordmanager.emailservice.domain.services.mail;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

public interface MailMessageService {
    void sendEmail(MimeMessage message);
    MimeMessage prepareMimeMessage(String destination, String subject, String body, Boolean isHtml) throws MessagingException;
}
