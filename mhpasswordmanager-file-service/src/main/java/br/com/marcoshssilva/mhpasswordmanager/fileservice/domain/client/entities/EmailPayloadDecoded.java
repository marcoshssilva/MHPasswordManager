package br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.client.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString
public class EmailPayloadDecoded extends AbstractKeyPayloadDecoded {
    private static final long serialVersionUID = 1L;

    private String email;

    @JsonProperty("smtp_server")
    private String smtpServer;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("stored_passwords")
    private Set<PasswordPayloadDecoded> storedPasswords;

    @JsonProperty("stored_security_questions")
    private Set<SecurityQuestionsPayloadDecoded> storedSecurityQuestions;

    @JsonProperty("recovery_emails")
    private Set<String> recoveryEmails;

    @Override
    public PasswordKeyType getType() {
        return PasswordKeyType.EMAILS;
    }
}
