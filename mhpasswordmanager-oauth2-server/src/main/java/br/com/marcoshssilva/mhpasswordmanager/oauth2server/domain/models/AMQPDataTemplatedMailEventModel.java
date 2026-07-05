package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models;

import java.io.Serializable;
import java.util.Map;

@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.experimental.SuperBuilder
@lombok.ToString(callSuper = true)
@lombok.EqualsAndHashCode(callSuper = true)
public class AMQPDataTemplatedMailEventModel extends AMQPDataEventModel {
    private String template;
    private Map<String, ? extends Serializable> params;
}
