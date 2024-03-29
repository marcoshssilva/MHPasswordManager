package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.email;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.enums.PasswordKeyTypesEnum;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.util.SecurityQuestionsPayloadDecodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.AbstractKeyPayloadDecodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.util.PasswordPayloadDecodedDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

@SuperBuilder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class EmailPayloadDecodedDto extends AbstractKeyPayloadDecodedDto implements Serializable {
    public static final long serialVersionUID = 1L;

    @JsonProperty(required = true)
    private String email;

    @JsonProperty(value = "smtp_server")
    private String smtpServer;

    @JsonProperty(value = "phone_number")
    private String phoneNumber;

    @JsonProperty(required = true, value = "stored_passwords")
    private Set<PasswordPayloadDecodedDto> storedPasswords;

    @JsonProperty(value = "stored_security_questions")
    private Set<SecurityQuestionsPayloadDecodedDto> storedSecurityQuestions;

    @JsonProperty(value = "recovery_emails")
    private Set<String> recoveryEmails;


    @Override
    public PasswordKeyTypesEnum getType() {
        return PasswordKeyTypesEnum.EMAILS;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EmailPayloadDecodedDto that)) return false;
        return Objects.equals(email, that.email) && Objects.equals(smtpServer, that.smtpServer) && Objects.equals(phoneNumber, that.phoneNumber) && Objects.equals(storedPasswords, that.storedPasswords) && Objects.equals(storedSecurityQuestions, that.storedSecurityQuestions) && Objects.equals(recoveryEmails, that.recoveryEmails);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, smtpServer, phoneNumber, storedPasswords, storedSecurityQuestions, recoveryEmails);
    }

    @Override
    public String toString() {
        return "EmailPayloadDecodedDto{" +
                "email='" + email + '\'' +
                ", smtpServer='" + smtpServer + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", storedPasswords=" + storedPasswords +
                ", storedSecurityQuestions=" + storedSecurityQuestions +
                ", recoveryEmails=" + recoveryEmails +
                '}';
    }
}
