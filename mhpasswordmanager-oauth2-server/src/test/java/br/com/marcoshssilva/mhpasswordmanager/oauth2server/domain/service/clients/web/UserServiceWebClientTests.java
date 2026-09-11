package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.clients.web;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.RecoveryPasswordCodeRequest;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.clients.entities.AccountCreateRequestData;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.clients.entities.AccountExistsResponseData;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.clients.entities.AccountRecoveryPasswordCodeRequestData;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.clients.entities.AccountResetPasswordRequestData;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.clients.entities.AccountResponseData;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.clients.entities.AccountUpdateEnabledRequestData;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.clients.entities.AccountUserInternalResponseData;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.clients.entities.AccountValidatePasswordRequestData;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.clients.entities.AccountValidatePasswordResponseData;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.clients.entities.AccountVerificationCodeResponseData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.UUID;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserServiceWebClientTests {

    @Mock
    private WebClient webClient;

    @Mock
    @SuppressWarnings("rawtypes")
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    @SuppressWarnings("rawtypes")
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    @SuppressWarnings("rawtypes")
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    @SuppressWarnings("rawtypes")
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    private UserServiceWebClient client;

    @BeforeEach
    void setUp() {
        client = new UserServiceWebClient(webClient, Duration.ofSeconds(5));
    }

    @DisplayName("Should create account")
    @Test
    void shouldCreateAccount() {
        AccountCreateRequestData request = AccountCreateRequestData.builder().username("testuser").build();

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/account/create")).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(request)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(Mono.just(ResponseEntity.ok().build()));

        assertDoesNotThrow(() -> client.createAccount(request));
    }

    @DisplayName("Should get account data by username")
    @Test
    void shouldGetAccountDataByUsername() {
        AccountResponseData expected = AccountResponseData.builder().username("testuser").email("test@example.com").build();

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/account/{username}/data", "testuser")).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(AccountResponseData.class)).thenReturn(Mono.just(expected));

        AccountResponseData result = client.getAccountData("testuser");
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @DisplayName("Should get account data by email")
    @Test
    @SuppressWarnings("unchecked")
    void shouldGetAccountDataByEmail() {
        AccountResponseData expected = AccountResponseData.builder().username("testuser").email("test@example.com").build();

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(AccountResponseData.class)).thenReturn(Mono.just(expected));

        AccountResponseData result = client.getAccountDataByEmail("test@example.com");
        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
    }

    @DisplayName("Should check account exists by username")
    @Test
    @SuppressWarnings("unchecked")
    void shouldCheckAccountExistsByUsername() {
        AccountExistsResponseData response = AccountExistsResponseData.builder().exists(true).build();

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(AccountExistsResponseData.class)).thenReturn(Mono.just(response));

        boolean exists = client.accountExistsByUsername("testuser");
        assertTrue(exists);
    }

    @DisplayName("Should check account exists by email")
    @Test
    @SuppressWarnings("unchecked")
    void shouldCheckAccountExistsByEmail() {
        AccountExistsResponseData response = AccountExistsResponseData.builder().exists(false).build();

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(AccountExistsResponseData.class)).thenReturn(Mono.just(response));

        boolean exists = client.accountExistsByEmail("test@example.com");
        assertFalse(exists);
    }

    @DisplayName("Should get internal user from account successfully")
    @Test
    void shouldGetInternalUserFromAccount() {
        AccountUserInternalResponseData expected = AccountUserInternalResponseData.builder().username("testuser").build();

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/account/{username}/user", "testuser")).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(AccountUserInternalResponseData.class)).thenReturn(Mono.just(expected));

        AccountUserInternalResponseData result = client.getInternalUserFromAccount("testuser");
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @DisplayName("Should return null when internal user not found (404)")
    @Test
    void shouldReturnNullWhenInternalUserNotFound() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/account/{username}/user", "missing")).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(AccountUserInternalResponseData.class))
                .thenReturn(Mono.error(WebClientResponseException.create(HttpStatus.NOT_FOUND.value(), "Not Found", HttpHeaders.EMPTY, new byte[0], null)));

        AccountUserInternalResponseData result = client.getInternalUserFromAccount("missing");
        assertNull(result);
    }

    @DisplayName("Should validate password")
    @Test
    void shouldValidatePassword() {
        AccountValidatePasswordResponseData response = AccountValidatePasswordResponseData.builder().isValid(true).build();

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/account/{username}/validatePassword", "testuser")).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any(AccountValidatePasswordRequestData.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(AccountValidatePasswordResponseData.class)).thenReturn(Mono.just(response));

        boolean valid = client.validatePassword("testuser", "pass123");
        assertTrue(valid);
    }

    @DisplayName("Should delete account")
    @Test
    void shouldDeleteAccount() {
        when(webClient.delete()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/account/{username}/delete", "testuser")).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(Mono.just(ResponseEntity.ok().build()));

        assertDoesNotThrow(() -> client.deleteAccount("testuser"));
    }

    @DisplayName("Should update account enabled")
    @Test
    void shouldUpdateAccountEnabled() {
        when(webClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/account/{username}/updateEnabled", "testuser")).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any(AccountUpdateEnabledRequestData.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(Mono.just(ResponseEntity.ok().build()));

        assertDoesNotThrow(() -> client.updateAccountEnabled("testuser", true));
    }

    @DisplayName("Should reset account password")
    @Test
    void shouldResetAccountPassword() {
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/account/{username}/resetPassword", "testuser")).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any(AccountResetPasswordRequestData.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(Mono.just(ResponseEntity.ok().build()));

        assertDoesNotThrow(() -> client.resetAccountPassword("testuser", "newPassword123"));
    }

    @DisplayName("Should generate account verification code")
    @Test
    void shouldGenerateAccountVerificationCode() {
        UUID code = UUID.randomUUID();
        AccountVerificationCodeResponseData response = AccountVerificationCodeResponseData.builder().code(code).build();

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/account/{username}/verificationCode", "testuser")).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(AccountVerificationCodeResponseData.class)).thenReturn(Mono.just(response));

        UUID result = client.generateAccountVerificationCode("testuser");
        assertEquals(code, result);
    }

    @DisplayName("Should throw IllegalStateException when account verification code is null")
    @Test
    void shouldThrowWhenVerificationCodeNull() {
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/account/{username}/verificationCode", "testuser")).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(AccountVerificationCodeResponseData.class)).thenReturn(Mono.just(new AccountVerificationCodeResponseData()));

        assertThrows(IllegalStateException.class, () -> client.generateAccountVerificationCode("testuser"));
    }

    @DisplayName("Should verify account")
    @Test
    void shouldVerifyAccount() {
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/account/verify/{uuidCode}", "uuid-123")).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Boolean.class)).thenReturn(Mono.just(Boolean.TRUE));

        Boolean result = client.verifyAccount("uuid-123");
        assertTrue(result);
    }

    @DisplayName("Should save recovery password code")
    @Test
    void shouldSaveRecoveryPasswordCode() {
        AccountRecoveryPasswordCodeRequestData req = AccountRecoveryPasswordCodeRequestData.builder().code("12345678901").build();
        RecoveryPasswordCodeRequest expected = RecoveryPasswordCodeRequest.builder().code("12345678901").build();

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/account/recoveryPasswordCode")).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(req)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(RecoveryPasswordCodeRequest.class)).thenReturn(Mono.just(expected));

        RecoveryPasswordCodeRequest result = client.saveRecoveryPasswordCode(req);
        assertNotNull(result);
        assertEquals("12345678901", result.getCode());
    }

    @DisplayName("Should find recovery password code")
    @Test
    @SuppressWarnings("unchecked")
    void shouldFindRecoveryPasswordCode() {
        RecoveryPasswordCodeRequest expected = RecoveryPasswordCodeRequest.builder().code("12345678901").build();

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(RecoveryPasswordCodeRequest.class)).thenReturn(Mono.just(expected));

        RecoveryPasswordCodeRequest result = client.findRecoveryPasswordCode("12345678901", "127.0.0.1", "Mozilla");
        assertNotNull(result);
        assertEquals("12345678901", result.getCode());
    }
}
