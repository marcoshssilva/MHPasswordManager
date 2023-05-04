package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.website;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.enums.PasswordKeyTypesEnum;

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
public class WebsitePayloadDecodedDto extends AbstractKeyPayloadDecodedDto implements Serializable {
    public static final long serialVersionUID = 1L;

    @JsonProperty(defaultValue = "")
    private String email;

    @JsonProperty(defaultValue = "")
    private String username;

    @JsonProperty(required = true, value = "stored_passwords")
    private Set<PasswordPayloadDecodedDto> storedPasswords;

    @Override
    public PasswordKeyTypesEnum getType() {
        return PasswordKeyTypesEnum.WEBSITE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WebsitePayloadDecodedDto that)) return false;
        return Objects.equals(email, that.email) && Objects.equals(username, that.username) && Objects.equals(storedPasswords, that.storedPasswords);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, username, storedPasswords);
    }

    @Override
    public String toString() {
        return "WebsitePayloadDecodedDto{" +
                "email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", storedPasswords=" + storedPasswords +
                '}';
    }
}
