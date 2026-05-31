package br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.client.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString
public class EmailPasswordStoredValue extends AbstractPasswordStoredValueDecoded {
    private static final long serialVersionUID = 1L;

    private String email;

    @JsonProperty("smtp_server")
    private String smtpServer;

    @JsonProperty("phone_number")
    private String phoneNumber;
}
