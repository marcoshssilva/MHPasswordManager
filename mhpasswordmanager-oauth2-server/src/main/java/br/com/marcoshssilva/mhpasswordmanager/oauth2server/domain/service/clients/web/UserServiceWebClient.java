package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.clients.web;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.configuration.UserServiceWebClientProperties;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.clients.entities.AccountCreateRequestData;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.RecoveryPasswordCodeRequest;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.clients.entities.AccountExistsResponseData;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.clients.entities.AccountRecoveryPasswordCodeRequestData;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.clients.entities.AccountResetPasswordRequestData;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.clients.entities.AccountResponseData;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.clients.entities.AccountUpdateEnabledRequestData;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.clients.entities.AccountUserInternalResponseData;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.clients.entities.AccountValidatePasswordRequestData;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.clients.entities.AccountValidatePasswordResponseData;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.clients.entities.AccountVerificationCodeResponseData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.UUID;

@Component
@ConditionalOnProperty(name = "config.users.mode", havingValue = "WEB_CLIENT")
public final class UserServiceWebClient {
    private static final String ACCOUNT_PATH = "/account";
    private final WebClient webClient;
    private final Duration responseTimeoutDuration;

    public UserServiceWebClient(@LoadBalanced WebClient.Builder webClientBuilder, UserServiceWebClientProperties properties, OAuth2AuthorizedClientManager userServiceOAuth2AuthorizedClientManager, @Value("${config.users.webclient.request-timeout:3s}") Duration responseTimeoutDuration) {
        this.responseTimeoutDuration = responseTimeoutDuration;
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
                .timeout(responseTimeoutDuration)
                .block();
    }

    public AccountResponseData getAccountData(String username) {
        try {
            return webClient.get()
                    .uri(ACCOUNT_PATH + "/{username}/data", username)
                    .retrieve()
                    .bodyToMono(AccountResponseData.class)
                    .timeout(responseTimeoutDuration)
                    .block();
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return null;
            }
            throw e;
        }
    }

    public AccountResponseData getAccountDataByEmail(String email) {
        try {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder.path(ACCOUNT_PATH + "/byEmail").queryParam("email", email).build())
                    .retrieve()
                    .bodyToMono(AccountResponseData.class)
                    .timeout(responseTimeoutDuration)
                    .block();
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return null;
            }
            throw e;
        }
    }

    public boolean accountExistsByUsername(String username) {
        AccountExistsResponseData response = webClient.get()
                .uri(uriBuilder -> uriBuilder.path(ACCOUNT_PATH + "/exists").queryParam("username", username).build())
                .retrieve()
                .bodyToMono(AccountExistsResponseData.class)
                .timeout(responseTimeoutDuration)
                .block();
        return response != null && Boolean.TRUE.equals(response.getExists());
    }

    public boolean accountExistsByEmail(String email) {
        AccountExistsResponseData response = webClient.get()
                .uri(uriBuilder -> uriBuilder.path(ACCOUNT_PATH + "/exists").queryParam("email", email).build())
                .retrieve()
                .bodyToMono(AccountExistsResponseData.class)
                .timeout(responseTimeoutDuration)
                .block();
        return response != null && Boolean.TRUE.equals(response.getExists());
    }

    public AccountUserInternalResponseData getInternalUserFromAccount(String username) {
        try {
            return webClient.get()
                    .uri(ACCOUNT_PATH + "/{username}/user", username)
                    .retrieve()
                    .bodyToMono(AccountUserInternalResponseData.class)
                    .timeout(responseTimeoutDuration)
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
                .timeout(responseTimeoutDuration)
                .block();

        return response != null && Boolean.TRUE.equals(response.getIsValid());
    }

    public void deleteAccount(String username) {
        webClient.delete()
                .uri(ACCOUNT_PATH + "/{username}/delete", username)
                .retrieve()
                .toBodilessEntity()
                .timeout(responseTimeoutDuration)
                .block();
    }

    public void updateAccountEnabled(String username, Boolean enabled) {
        webClient.put()
                .uri(ACCOUNT_PATH + "/{username}/updateEnabled", username)
                .bodyValue(AccountUpdateEnabledRequestData.builder().enabled(enabled).build())
                .retrieve()
                .toBodilessEntity()
                .timeout(responseTimeoutDuration)
                .block();
    }

    public void resetAccountPassword(String username, String newPassword) {
        webClient.post()
                .uri(ACCOUNT_PATH + "/{username}/resetPassword", username)
                .bodyValue(AccountResetPasswordRequestData.builder().newPassword(newPassword).build())
                .retrieve()
                .toBodilessEntity()
                .timeout(responseTimeoutDuration)
                .block();
    }

    public UUID generateAccountVerificationCode(String username) {
        AccountVerificationCodeResponseData response = webClient.post()
                .uri(ACCOUNT_PATH + "/{username}/verificationCode", username)
                .retrieve()
                .bodyToMono(AccountVerificationCodeResponseData.class)
                .timeout(responseTimeoutDuration)
                .block();
        if (response == null || response.getCode() == null) {
            throw new IllegalStateException("User service returned no account verification code.");
        }
        return response.getCode();
    }

    public Boolean verifyAccount(String uuidCode) {
        return webClient.post()
                .uri(ACCOUNT_PATH + "/verify/{uuidCode}", uuidCode)
                .retrieve()
                .bodyToMono(Boolean.class)
                .timeout(responseTimeoutDuration)
                .block();
    }

    public RecoveryPasswordCodeRequest saveRecoveryPasswordCode(AccountRecoveryPasswordCodeRequestData request) {
        return webClient.post()
                .uri(ACCOUNT_PATH + "/recoveryPasswordCode")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(RecoveryPasswordCodeRequest.class)
                .timeout(responseTimeoutDuration)
                .block();
    }

    public RecoveryPasswordCodeRequest findRecoveryPasswordCode(String code, String ipClient, String userAgentClient) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path(ACCOUNT_PATH + "/recoveryPasswordCode/{code}")
                        .queryParam("ipClient", ipClient)
                        .queryParam("userAgentClient", userAgentClient)
                        .build(code))
                .retrieve()
                .bodyToMono(RecoveryPasswordCodeRequest.class)
                .timeout(responseTimeoutDuration)
                .block();
    }
}
