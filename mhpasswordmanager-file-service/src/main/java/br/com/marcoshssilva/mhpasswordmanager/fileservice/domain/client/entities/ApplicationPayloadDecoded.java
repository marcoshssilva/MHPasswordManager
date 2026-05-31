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
public class ApplicationPayloadDecoded extends AbstractKeyPayloadDecoded {
    private static final long serialVersionUID = 1L;

    @JsonProperty("app_name")
    private String appName;
    private Set<AppData> credentials;

    @Override
    public PasswordKeyType getType() {
        return PasswordKeyType.APPLICATION;
    }
}
