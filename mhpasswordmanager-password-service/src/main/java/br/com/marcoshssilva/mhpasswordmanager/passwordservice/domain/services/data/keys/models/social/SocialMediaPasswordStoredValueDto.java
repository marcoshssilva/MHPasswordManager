package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.social;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.AbstractPasswordStoredValueDecodedDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@SuperBuilder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class SocialMediaPasswordStoredValueDto extends AbstractPasswordStoredValueDecodedDto implements Serializable {
    public static final long serialVersionUID = 1L;

    @JsonProperty(required = true)
    private String email;

    @JsonProperty(required = true)
    private String username;

    @JsonProperty(value = "profile_url")
    private String profileUrl;

    @JsonProperty(value = "phone_number")
    private String phoneNumber;
}
