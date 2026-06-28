package br.com.marcoshssilva.mhpasswordmanager.emailservice.amqp.models;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.ToString
@lombok.EqualsAndHashCode
public class AMQPEventModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String identifier;
    private List<String> tags;
    private AMQPDataEventModel data;
}
