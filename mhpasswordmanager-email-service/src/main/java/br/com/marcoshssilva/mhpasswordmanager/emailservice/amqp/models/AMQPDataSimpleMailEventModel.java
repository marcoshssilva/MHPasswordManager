package br.com.marcoshssilva.mhpasswordmanager.emailservice.amqp.models;

@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Builder
@lombok.ToString(callSuper = true)
@lombok.EqualsAndHashCode(callSuper = true)
public class AMQPDataSimpleMailEventModel extends AMQPDataEventModel {
    private String body;
    private Boolean isHtml;
}
