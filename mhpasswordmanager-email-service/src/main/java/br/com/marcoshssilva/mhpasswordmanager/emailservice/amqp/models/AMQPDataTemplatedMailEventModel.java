package br.com.marcoshssilva.mhpasswordmanager.emailservice.amqp.models;

import java.util.Map;

@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Builder
@lombok.ToString(callSuper = true)
@lombok.EqualsAndHashCode(callSuper = true)
public class AMQPDataTemplatedMailEventModel extends AMQPDataEventModel {
    private String template;
    private Map<String, Object> params;
}
