package br.com.marcoshssilva.mhpasswordmanager.oauth2server.configuration;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("authorization")
@RefreshScope
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class AuthorizationConfigProperties {
    private String issuerUri;
    private String successLogoutUri;
    private String jwkPrivateKey;
    private String jwkPublicKey;
    private String jwkUuid;
}
