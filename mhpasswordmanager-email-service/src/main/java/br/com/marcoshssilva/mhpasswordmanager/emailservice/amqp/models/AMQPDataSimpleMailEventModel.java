package br.com.marcoshssilva.mhpasswordmanager.emailservice.amqp.models;

@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.ToString(callSuper = true)
@lombok.EqualsAndHashCode(callSuper = true)
@lombok.experimental.SuperBuilder
public class AMQPDataSimpleMailEventModel extends AMQPDataEventModel {
    private String body;
    private Boolean isHtml;
}
