package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.email;

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
public class EmailPasswordStoredValueDto extends AbstractPasswordStoredValueDecodedDto implements Serializable {
    public static final long serialVersionUID = 1L;

    @JsonProperty(required = true)
    private String email;

    @JsonProperty(value = "smtp_server", required = true)
    private String smtpServer;

    @JsonProperty(value = "phone_number", required = true)
    private String phoneNumber;
}
