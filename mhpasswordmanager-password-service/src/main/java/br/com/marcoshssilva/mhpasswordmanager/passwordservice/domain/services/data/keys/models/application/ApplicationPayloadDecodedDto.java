package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.application;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.enums.PasswordKeyTypesEnum;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.AbstractKeyPayloadDecodedDto;

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
public class ApplicationPayloadDecodedDto extends AbstractKeyPayloadDecodedDto implements Serializable {
    public static final long serialVersionUID = 1L;

    @JsonProperty(required = true, value = "app_name")
    private String appName;
    @JsonProperty(required = true)
    private Set<AppDataDto> credentials;

    @Override
    public PasswordKeyTypesEnum getType() {
        return PasswordKeyTypesEnum.APPLICATION;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ApplicationPayloadDecodedDto that)) return false;
        return Objects.equals(appName, that.appName) && Objects.equals(credentials, that.credentials);
    }

    @Override
    public int hashCode() {
        return Objects.hash(appName, credentials);
    }

    @Override
    public String toString() {
        return "ApplicationPayloadDecodedDto{" +
                "appName='" + appName + '\'' +
                ", credentials=" + credentials +
                '}';
    }
}
