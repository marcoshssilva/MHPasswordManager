package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.ToString
@lombok.EqualsAndHashCode
@lombok.experimental.SuperBuilder
public class AMQPMailEventModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String identifier;
    private List<String> tags;
    private AMQPDataEventModel data;
}
