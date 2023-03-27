package br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

import java.time.Duration;
import java.util.UUID;

@Configuration
public class RegisteredClientRepositoryConfig {

    @Bean
    @Profile("!in-memory-client & !embedded-database")
    public RegisteredClientRepository dbAuthJdbcRegisteredClientRepository(@Qualifier("dbAuthJdbcTemplate") JdbcTemplate jdbcTemplate) {
        return new JdbcRegisteredClientRepository(jdbcTemplate);
    }

    @Bean
    @Profile("embedded-database")
    public RegisteredClientRepository embeddedJdbcRegisteredClientRepository(@Qualifier("embeddedJdbcTemplate") JdbcTemplate jdbcTemplate) {
        return new JdbcRegisteredClientRepository(jdbcTemplate);
    }

    @Bean
    @Profile("in-memory-client")
    public RegisteredClientRepository inMemoryRegisteredClientRepository(PasswordEncoder encoder) {
        RegisteredClient defaultClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientName("Registered client for Client Credentials")
                .clientId("MHPasswordManager-GlobalAdmin")
                .clientSecret(encoder.encode("8e18ee56-ab7c-4ed9-b192-ff4472e5c697"))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)

                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofMinutes(3L))
                        .build())

                .scope("user:canRead")
                .scope("user:canWrite")
                .scope("user:canDelete")
                .scope("user:canCreate")

                .build();

        return new InMemoryRegisteredClientRepository(defaultClient);
    }
}
