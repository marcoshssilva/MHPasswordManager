package br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
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
    private final Logger LOG = LoggerFactory.getLogger(RegisteredClientRepositoryConfig.class);


    @Bean
    @Profile("!in-memory-client & !embedded-database")
    public RegisteredClientRepository dbAuthJdbcRegisteredClientRepository(@Qualifier("dbAuthJdbcTemplate") JdbcTemplate jdbcTemplate) {
        LOG.info("Using JdbcRegisteredClient with Auth-Datasource");
        return new JdbcRegisteredClientRepository(jdbcTemplate);
    }

    @Bean
    @Profile("embedded-database")
    public RegisteredClientRepository embeddedJdbcRegisteredClientRepository(@Qualifier("embeddedJdbcTemplate") JdbcTemplate jdbcTemplate) {
        LOG.info("Using JdbcRegisteredClient with Embedded-Database");
        return new JdbcRegisteredClientRepository(jdbcTemplate);
    }

    @Bean
    @Profile("in-memory-client")
    public RegisteredClientRepository inMemoryRegisteredClientRepository(PasswordEncoder encoder) {
        RegisteredClient defaultAuthorizationCode = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientName("Registered client for PWA Client")
                .clientId("MHPasswordManager")
                .clientSecret(encoder.encode("fd04f93e-5e4d-4f16-98ae-9247f68d8619"))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)

                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)

                .clientSettings(ClientSettings.builder()
                        .requireAuthorizationConsent(true)
                        .requireProofKey(false)
                        .build())

                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofMinutes(15L))
                        .reuseRefreshTokens(false)
                        .refreshTokenTimeToLive(Duration.ofHours(3L))
                        .build())

                .redirectUri("https://oidcdebugger.com/debug")
                .redirectUri("https://oauth.pstmn.io/v1/callback")
                .redirectUri("http://127.0.0.1:8100/authorize")
                .redirectUri("http://127.0.0.1:12050/users/swagger-ui/redirect")
                .redirectUri("http://127.0.0.1:12050/passwords/swagger-ui/redirect")
                .redirectUri("http://127.0.0.1:12050/emails/swagger-ui/redirect")
                .redirectUri("http://127.0.0.1:12050/documents/swagger-ui/redirect")
                .redirectUri("http://127.0.0.1:12050/log/swagger-ui/redirect")

                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .scope(OidcScopes.EMAIL)

                .scope("user:canSelfRead")
                .scope("user:canSelfWrite")
                .scope("user:canSelfDelete")

                .build();


        RegisteredClient defaultClientCredentials = RegisteredClient.withId(UUID.randomUUID().toString())
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

        LOG.info("Using InMemoryRegisteredClient with default clients: {} {}", defaultClientCredentials, defaultAuthorizationCode);
        return new InMemoryRegisteredClientRepository(defaultClientCredentials, defaultAuthorizationCode);
    }
}
