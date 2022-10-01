package br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.ClientSettings;
import org.springframework.security.oauth2.server.authorization.config.TokenSettings;

import java.time.Duration;
import java.util.UUID;

@Configuration
public class RegisteredClientRepositoryConfig {

    @Bean
    @Primary
    public RegisteredClientRepository jdbcRegisteredClientRepository(JdbcTemplate jdbcTemplate) {
        return new JdbcRegisteredClientRepository(jdbcTemplate);
    }

    @Bean
    public RegisteredClientRepository inMemoryRegisteredClientRepository() {
        RegisteredClient clientDefault = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("master")
                .clientSecret("masterSecret")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri("http://localhost:4200/authorize")
                .redirectUri("http://localhost:8100/authorize")
                .clientName("ClientMasterSystem")
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofDays(1))
                        .reuseRefreshTokens(true)
                        .build())
                .clientSettings(ClientSettings.builder()
                        .requireAuthorizationConsent(false)
                        .requireProofKey(false)
                        .build())
                .build();

        return new InMemoryRegisteredClientRepository(clientDefault);
    }
}
