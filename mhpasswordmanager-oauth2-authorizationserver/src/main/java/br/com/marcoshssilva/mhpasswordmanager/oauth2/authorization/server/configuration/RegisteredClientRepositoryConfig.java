package br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.ClientSettings;

import java.util.Arrays;
import java.util.UUID;

@Configuration
public class RegisteredClientRepositoryConfig {

    @Bean
    public RegisteredClientRepository registeredClientRepository(JdbcTemplate jdbcTemplate, PasswordEncoder encoder) {

        RegisteredClient registeredClientWithAuthorizationCode = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("ClientWithAuthorizationCode")
                .clientSecret(encoder.encode("password"))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri("http://localhost:8100/authorize")
                .redirectUri("https://oidcdebugger.com/debug")
                .scope("user:read")
                .scope("user:write")
                .clientSettings(
                        ClientSettings.builder().requireAuthorizationConsent(false)
                                .build())
                .build();

        RegisteredClient registeredClientWithClientCredentials = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("ClientWithClientCredentials")
                .clientSecret(encoder.encode("password"))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .redirectUri("http://localhost:8100/authorize")
                .redirectUri("https://oidcdebugger.com/debug")
                .clientSettings(
                        ClientSettings.builder().requireAuthorizationConsent(false)
                                .build())
                .build();

        JdbcRegisteredClientRepository registeredClientRepository = new JdbcRegisteredClientRepository(jdbcTemplate);
        // Save registered client in db as if in-memory
        Arrays.asList(registeredClientWithClientCredentials, registeredClientWithAuthorizationCode)
                .forEach(registeredClientRepository::save);

        return registeredClientRepository;
    }

}
