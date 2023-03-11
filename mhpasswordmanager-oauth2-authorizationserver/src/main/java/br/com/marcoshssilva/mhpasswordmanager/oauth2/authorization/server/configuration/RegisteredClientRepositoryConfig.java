package br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.configuration;

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
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

import java.time.Duration;
import java.util.UUID;

@Configuration
public class RegisteredClientRepositoryConfig {

    @Bean
    @Profile("!test")
    public RegisteredClientRepository jdbcRegisteredClientRepository(JdbcTemplate jdbcTemplate) {
        return new JdbcRegisteredClientRepository(jdbcTemplate);
    }

    @Bean
    @Profile("test")
    public RegisteredClientRepository inMemoryRegisteredClientRepository(PasswordEncoder encoder) {
        RegisteredClient register1 = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientName("Registered client for PWA Client")
                .clientId("MHPasswordManager")
                .clientSecret(encoder.encode("fd04f93e-5e4d-4f16-98ae-9247f68d8619"))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)

                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)

                .redirectUri("https://oidcdebugger.com/debug")
                .redirectUri("https://oauth.pstmn.io/v1/callback")

                .scope("user:canSelfRead")
                .scope("user:canSelfWrite")
                .scope("user:canSelfDelete")

                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofMinutes(15L))
                        .reuseRefreshTokens(false)
                        .refreshTokenTimeToLive(Duration.ofHours(3L))
                        .build())
                .clientSettings(ClientSettings.builder()
                        .requireAuthorizationConsent(true)
                        .requireProofKey(false)
                        .build())
                .build();

        RegisteredClient register2 = RegisteredClient.withId(UUID.randomUUID().toString())
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

        return new InMemoryRegisteredClientRepository(register1, register2);
    }
}
