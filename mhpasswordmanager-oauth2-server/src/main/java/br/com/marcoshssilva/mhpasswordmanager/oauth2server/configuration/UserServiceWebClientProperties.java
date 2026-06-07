package br.com.marcoshssilva.mhpasswordmanager.oauth2server.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@ConfigurationProperties(prefix = "config.users.webclient")
public class UserServiceWebClientProperties {
    private String baseUrl = "http://USER-SERVICE/mypass-manager/users";
    private OAuth2 oauth2 = new OAuth2();
    @Getter
    @Setter
    public static class OAuth2 {
        private String registrationId;
        private String clientId;
        private String clientSecret;
        private String tokenUri;
        private Set<String> scopes = new LinkedHashSet<>();
    }
}
