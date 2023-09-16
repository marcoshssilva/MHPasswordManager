package br.com.marcoshssilva.mhpasswordmanager.emailservice.amqp.listeners;

import br.com.marcoshssilva.mhpasswordmanager.emailservice.domain.models.RecoveryCodeUserMailMessage;
import br.com.marcoshssilva.mhpasswordmanager.emailservice.domain.services.html.HTMLTemplateEngineService;
import br.com.marcoshssilva.mhpasswordmanager.emailservice.domain.services.mail.MailMessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

@Slf4j
@RequiredArgsConstructor
@Component
public class QueueSendRecoveryCode {
    public static final String DEFAULT_TITLE = "%s, aqui está seu código de confirmação";

    private final MailMessageService mailMessageService;
    private final HTMLTemplateEngineService htmlTemplateEngineService;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = {"email.send-recovery-code"})
    public void onReceiveMessage(@Payload Message message) {
        try {
            String content = new String(message.getBody(), StandardCharsets.UTF_8);
            log.debug("Received message: {}", content);

            RecoveryCodeUserMailMessage data = objectMapper.readValue(message.getBody(), RecoveryCodeUserMailMessage.class);
            HashMap<String, Object> map = new HashMap<>();
            map.put("data", data);
            String mailMessage = htmlTemplateEngineService.prepareHtmlMailMessage("confirm-recovery-code", map);
            MimeMessage mimeMessage = mailMessageService.prepareMimeMessage(data.getEmail(), String.format(DEFAULT_TITLE, data.getName()), mailMessage, true, false);

            mailMessageService.sendEmail(mimeMessage);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
