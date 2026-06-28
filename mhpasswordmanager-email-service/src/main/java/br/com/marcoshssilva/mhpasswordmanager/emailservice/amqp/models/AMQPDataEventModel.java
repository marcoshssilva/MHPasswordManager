package br.com.marcoshssilva.mhpasswordmanager.emailservice.amqp.models;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serial;
import java.io.Serializable;

@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.ToString
@lombok.EqualsAndHashCode
@lombok.experimental.SuperBuilder

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes({
    @JsonSubTypes.Type(value = AMQPDataSimpleMailEventModel.class, name = "SIMPLE"),
    @JsonSubTypes.Type(value = AMQPDataTemplatedMailEventModel.class, name = "TEMPLATED")
})
public abstract class AMQPDataEventModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String to;
    private String subject;
}
