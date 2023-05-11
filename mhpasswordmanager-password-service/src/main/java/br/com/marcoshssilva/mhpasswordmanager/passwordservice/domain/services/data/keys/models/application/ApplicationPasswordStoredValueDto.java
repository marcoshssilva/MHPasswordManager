package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.application;

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
public class ApplicationPasswordStoredValueDto extends AbstractPasswordStoredValueDecodedDto implements Serializable {
    public static final long serialVersionUID = 1L;

    @JsonProperty(value = "app_name", required = true)
    private String appName;

    @JsonProperty(required = true)
    private String username;

}
