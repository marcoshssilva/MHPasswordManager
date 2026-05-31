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
public class WebsitePayloadDecoded extends AbstractKeyPayloadDecoded {
    private static final long serialVersionUID = 1L;

    private String url;
    private String email;
    private String username;

    @JsonProperty("stored_passwords")
    private Set<PasswordPayloadDecoded> storedPasswords;

    @Override
    public PasswordKeyType getType() {
        return PasswordKeyType.WEBSITE;
    }
}
