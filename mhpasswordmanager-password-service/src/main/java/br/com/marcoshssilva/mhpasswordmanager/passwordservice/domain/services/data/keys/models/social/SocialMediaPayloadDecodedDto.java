package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.social;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.enums.PasswordKeyTypesEnum;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.AbstractKeyPayloadDecodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.util.PasswordPayloadDecodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.util.SecurityQuestionsPayloadDecodedDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

@SuperBuilder
@Getter
@AllArgsConstructor
@Setter
public class SocialMediaPayloadDecodedDto extends AbstractKeyPayloadDecodedDto implements Serializable {
    public static final long serialVersionUID = 1L;

    private String profileUrl;
    private String email;
    private String username;
    private String phoneNumber;

    private Set<PasswordPayloadDecodedDto> storedPasswords;
    private Set<SecurityQuestionsPayloadDecodedDto> storedSecurityQuestions;

    @Override
    protected PasswordKeyTypesEnum getType() {
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
