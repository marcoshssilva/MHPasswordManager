package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class KeyPayloadUpdateDto implements Serializable {
    public static final long serialVersionUID = 1L;

    @JsonProperty(required = true)
    private String[] tags;

    @JsonProperty(required = true)
    private String description;
}
