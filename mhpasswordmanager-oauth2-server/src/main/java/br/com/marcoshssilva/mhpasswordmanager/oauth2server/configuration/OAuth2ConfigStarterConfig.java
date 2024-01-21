package br.com.marcoshssilva.mhpasswordmanager.oauth2server.configuration;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.OAuthClient;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.OAuthUser;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.provisioning.UserDetailsManager;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class OAuth2ConfigStarterConfig {
    private static final Logger LOG = LoggerFactory.getLogger(OAuth2ConfigStarterConfig.class);

    private final OAuth2ConfigStarterPropertiesConfig.OAuth2ConfigStarterProperties oAuth2ConfigStarterProperties;
    private final RegisteredClientRepository registeredClientRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsManager userDetailsManager;

    @PostConstruct
    public void init() {

        List<OAuthClient> clients = oAuth2ConfigStarterProperties.getClients();
        clients.forEach(client -> {
            Optional<RegisteredClient> existentClient = Optional.ofNullable(registeredClientRepository.findByClientId(client.getClientId()));
            if (existentClient.isEmpty()) {
                RegisteredClient clientCreated = registeredClientBuilder(client).build();
                registeredClientRepository.save(clientCreated);
                LOG.info("Register new client, details: ID - {} SECRET - {}", clientCreated.getClientId(), clientCreated.getClientSecret());
            } else {
                LOG.info("Registered client with ID {} exists. Cannot be updated.", existentClient.get().getClientId());
            }

        });

        List<OAuthUser> users = oAuth2ConfigStarterProperties.getUsers();
        users.forEach(user -> {
            if (!userDetailsManager.userExists(user.getUsername())) {
                userDetailsManager.createUser(
                        User.builder()
                            .username(user.getUsername())
                            .password(user.getPassword())
                            .roles(user.getRoles().toArray(new String[] {}))
                            .accountExpired(!user.isAccountNonExpired())
                            .accountLocked(!user.isAccountNonLocked())
                            .credentialsExpired(!user.isCredentialsNonExpired())
                            .build());
                LOG.info("Request to create new user {} is ACCEPTED with roles={}", user.getUsername(), user.getRoles());
            } else {
                LOG.info("Request to create new user {} is REJECTED, because is already exists.", user.getUsername());
            }

        });
    }

    private RegisteredClient.Builder registeredClientBuilder(OAuthClient client) {
        return RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId(client.getClientId())
                .clientName(client.getClientName())
                .clientSecret(passwordEncoder.encode(client.getClientSecret()))
                .authorizationGrantTypes(consumer -> client.getAuthenticationGrantTypes().forEach(e -> consumer.add(e.getAuthorizationGrantType())))
                .clientAuthenticationMethod(client.getAuthenticationMethod().getClientAuthenticationMethod())
                .redirectUris(consumer -> consumer.addAll(client.getRedirectUris()))
                .scopes(consumer -> consumer.addAll(client.getScopes()))
                .clientSettings(ClientSettings.builder()
                        .requireAuthorizationConsent(client.getClientSettings().getRequireAuthorizationConsent())
                        .requireProofKey(client.getClientSettings().getRequireProofKey())
                        .build())
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofMillis(client.getTokenSettings().getDurationInMillis()))
                        .reuseRefreshTokens(client.getTokenSettings().getReuseRefreshToken())
                        .refreshTokenTimeToLive(Duration.ofMillis(client.getTokenSettings().getRefreshTokenTimeToLiveInMillis()))
                        .build());
    }
}
