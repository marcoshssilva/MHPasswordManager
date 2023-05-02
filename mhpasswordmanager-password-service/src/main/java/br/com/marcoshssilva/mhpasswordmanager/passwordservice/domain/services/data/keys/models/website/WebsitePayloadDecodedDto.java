package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.website;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.enums.PasswordKeyTypesEnum;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.AbstractKeyPayloadDecodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.util.PasswordPayloadDecodedDto;

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
public class WebsitePayloadDecodedDto extends AbstractKeyPayloadDecodedDto implements Serializable {
    public static final long serialVersionUID = 1L;

    private String email;
    private String username;

    private Set<PasswordPayloadDecodedDto> storedPasswords;

    @Override
    protected PasswordKeyTypesEnum getType() {
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
