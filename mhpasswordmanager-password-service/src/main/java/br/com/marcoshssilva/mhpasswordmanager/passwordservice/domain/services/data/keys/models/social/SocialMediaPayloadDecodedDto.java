package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.social;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.enums.PasswordKeyTypesEnum;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.AbstractKeyPayloadDecodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.util.PasswordPayloadDecodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.util.SecurityQuestionsPayloadDecodedDto;

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
public class SocialMediaPayloadDecodedDto extends AbstractKeyPayloadDecodedDto implements Serializable {
    public static final long serialVersionUID = 1L;

    @JsonProperty(value = "profile_url")
    private String profileUrl;
    private String username;

    @JsonProperty(required = true)
    private String email;

    @JsonProperty(value = "phone_number")
    private String phoneNumber;

    @JsonProperty(required = true, value = "stored_passwords")
    private Set<PasswordPayloadDecodedDto> storedPasswords;

    @JsonProperty(value = "stored_security_questions")
    private Set<SecurityQuestionsPayloadDecodedDto> storedSecurityQuestions;

    @Override
    public PasswordKeyTypesEnum getType() {
        return PasswordKeyTypesEnum.SOCIAL_MEDIA;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SocialMediaPayloadDecodedDto that)) return false;
        return Objects.equals(profileUrl, that.profileUrl) && Objects.equals(email, that.email) && Objects.equals(username, that.username) && Objects.equals(phoneNumber, that.phoneNumber) && Objects.equals(storedPasswords, that.storedPasswords) && Objects.equals(storedSecurityQuestions, that.storedSecurityQuestions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(profileUrl, email, username, phoneNumber, storedPasswords, storedSecurityQuestions);
    }

    @Override
    public String toString() {
        return "SocialMediaPayloadDecodedDto{" +
                "profileUrl='" + profileUrl + '\'' +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", storedPasswords=" + storedPasswords +
                ", storedSecurityQuestions=" + storedSecurityQuestions +
                '}';
    }
}
