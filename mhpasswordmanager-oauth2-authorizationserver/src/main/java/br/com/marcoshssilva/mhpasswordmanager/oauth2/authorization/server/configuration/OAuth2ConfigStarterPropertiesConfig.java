package br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.configuration;

import br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.domain.models.OAuthClient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedList;
import java.util.List;

@Configuration
public class OAuth2ConfigStarterPropertiesConfig {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class OAuth2ConfigStarterProperties {
        private List<OAuthClient> clients = new LinkedList<>();
    }

    @Bean
    @RefreshScope
    @ConfigurationProperties(prefix = "oauth-config-starter")
    public OAuth2ConfigStarterProperties oAuth2ConfigStarterProperties() {
        return new OAuth2ConfigStarterProperties();
    }
}
