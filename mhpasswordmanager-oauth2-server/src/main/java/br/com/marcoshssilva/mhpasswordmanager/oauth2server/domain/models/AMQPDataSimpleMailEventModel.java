package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models;

@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.experimental.SuperBuilder
@lombok.ToString(callSuper = true)
@lombok.EqualsAndHashCode(callSuper = true)
public class AMQPDataSimpleMailEventModel extends AMQPDataEventModel {
    private String body;
    private Boolean isHtml;
}
