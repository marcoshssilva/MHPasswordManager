package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.clients.web;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.configuration.UserServiceWebClientProperties;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.clients.entities.AccountCreateRequestData;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.clients.entities.AccountResetPasswordRequestData;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.clients.entities.AccountResponseData;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.clients.entities.AccountUpdateEnabledRequestData;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.clients.entities.AccountUserInternalResponseData;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.clients.entities.AccountValidatePasswordRequestData;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.clients.entities.AccountValidatePasswordResponseData;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
@ConditionalOnProperty(name = "config.users.mode", havingValue = "WEB_CLIENT")
public final class UserServiceWebClient {
    private static final String ACCOUNT_PATH = "/account";
    private final WebClient webClient;

    public UserServiceWebClient(@LoadBalanced WebClient.Builder webClientBuilder, UserServiceWebClientProperties properties, OAuth2AuthorizedClientManager userServiceOAuth2AuthorizedClientManager) {
        this.webClient = createWebClient(webClientBuilder, properties, userServiceOAuth2AuthorizedClientManager);
    }

    private WebClient createWebClient(WebClient.Builder webClientBuilder, UserServiceWebClientProperties properties, OAuth2AuthorizedClientManager userServiceOAuth2AuthorizedClientManager) {
        ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2 = new ServletOAuth2AuthorizedClientExchangeFilterFunction(userServiceOAuth2AuthorizedClientManager);
        oauth2.setDefaultClientRegistrationId(properties.getOauth2().getRegistrationId());
        return webClientBuilder.baseUrl(properties.getBaseUrl())
                .apply(oauth2.oauth2Configuration())
                .build();
    }

    public void createAccount(AccountCreateRequestData request) {
        webClient.post()
                .uri(ACCOUNT_PATH + "/create")
                .bodyValue(request)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public AccountResponseData getAccountData(String username) {
        try {
            return webClient.get()
                    .uri(ACCOUNT_PATH + "/{username}/data", username)
                    .retrieve()
                    .bodyToMono(AccountResponseData.class)
                    .block();
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return null;
            }
            throw e;
        }
    }

    public boolean accountExists(String username) {
        return getAccountData(username) != null;
    }

    public AccountUserInternalResponseData getInternalUserFromAccount(String username) {
        try {
            return webClient.get()
                    .uri(ACCOUNT_PATH + "/{username}/user", username)
                    .retrieve()
                    .bodyToMono(AccountUserInternalResponseData.class)
                    .block();
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return null;
            }
            throw e;
        }
    }

    public boolean validatePassword(String username, String password) {
        AccountValidatePasswordResponseData response = webClient.post()
                .uri(ACCOUNT_PATH + "/{username}/validatePassword", username)
                .bodyValue(AccountValidatePasswordRequestData.builder().password(password).build())
                .retrieve()
                .bodyToMono(AccountValidatePasswordResponseData.class)
                .block();

        return response != null && Boolean.TRUE.equals(response.getIsValid());
    }

    public void deleteAccount(String username) {
        webClient.delete()
                .uri(ACCOUNT_PATH + "/{username}/delete", username)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public void updateAccountEnabled(String username, Boolean enabled) {
        webClient.put()
                .uri(ACCOUNT_PATH + "/{username}/updateEnabled", username)
                .bodyValue(AccountUpdateEnabledRequestData.builder().enabled(enabled).build())
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public void resetAccountPassword(String username, String newPassword) {
        webClient.post()
                .uri(ACCOUNT_PATH + "/{username}/resetPassword", username)
                .bodyValue(AccountResetPasswordRequestData.builder().newPassword(newPassword).build())
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}
