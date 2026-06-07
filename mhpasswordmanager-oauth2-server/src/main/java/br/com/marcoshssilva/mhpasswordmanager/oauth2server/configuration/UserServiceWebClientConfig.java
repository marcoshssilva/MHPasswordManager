package br.com.marcoshssilva.mhpasswordmanager.oauth2server.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

@Configuration
@ConditionalOnProperty(name = "config.users.mode", havingValue = "WEB_CLIENT")
@EnableConfigurationProperties(UserServiceWebClientProperties.class)
public class UserServiceWebClientConfig {
    @Bean
    public ClientRegistrationRepository userServiceClientRegistrationRepository(UserServiceWebClientProperties properties) {
        return new InMemoryClientRegistrationRepository(
                ClientRegistration
                    .withRegistrationId(properties.getOauth2().getRegistrationId())
                    .clientId(properties.getOauth2().getClientId())
                    .clientSecret(properties.getOauth2().getClientSecret())
                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                    .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                    .tokenUri(properties.getOauth2().getTokenUri())
                    .scope(properties.getOauth2().getScopes())
                    .build());
    }

    @Bean
    public OAuth2AuthorizedClientManager userServiceOAuth2AuthorizedClientManager(ClientRegistrationRepository userServiceClientRegistrationRepository) {

        OAuth2AuthorizedClientProvider authorizedClientProvider =
                OAuth2AuthorizedClientProviderBuilder.builder().clientCredentials().build();
        InMemoryOAuth2AuthorizedClientService authorizedClientService
                = new InMemoryOAuth2AuthorizedClientService(userServiceClientRegistrationRepository);
        AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager
                = new AuthorizedClientServiceOAuth2AuthorizedClientManager(userServiceClientRegistrationRepository, authorizedClientService);

        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);
        return authorizedClientManager;
    }
}
