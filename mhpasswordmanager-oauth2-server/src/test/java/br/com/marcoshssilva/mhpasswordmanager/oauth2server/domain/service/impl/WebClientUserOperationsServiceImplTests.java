package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.impl;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.constants.UserRolesEnum;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.RecoveryPasswordCodeRequest;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.RegisteredUserData;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.RequestedBrowserParams;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.clients.entities.AccountCreateRequestData;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.clients.entities.AccountRecoveryPasswordCodeRequestData;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.clients.entities.AccountResponseData;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.clients.web.UserServiceWebClient;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.web.data.models.UserRegistrationData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WebClientUserOperationsServiceImplTests {

    @Mock
    private UserServiceWebClient userServiceWebClient;

    private WebClientUserOperationsServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new WebClientUserOperationsServiceImpl(userServiceWebClient);
    }

    @DisplayName("Should check if email used by another user")
    @Test
    void shouldCheckIfEmailUsed() {
        when(userServiceWebClient.accountExistsByEmail("test@example.com")).thenReturn(true);
        assertTrue(service.checkIfHasEmailUsedByAnotherUser("test@example.com"));
    }

    @DisplayName("Should check if username used by another user")
    @Test
    void shouldCheckIfUsernameUsed() {
        when(userServiceWebClient.accountExistsByUsername("testuser")).thenReturn(true);
        assertTrue(service.checkIfHasUsernameUsedByAnotherUser("testuser"));
    }

    @DisplayName("Should generate UUID code to check account mail verification")
    @Test
    void shouldGenerateUUIDCode() {
        UUID uuid = UUID.randomUUID();
        when(userServiceWebClient.generateAccountVerificationCode("testuser")).thenReturn(uuid);

        RegisteredUserData user = RegisteredUserData.builder().username("testuser").build();
        UUID result = service.generateUUIDCodeToCheckAccountMailVerification(user);

        assertEquals(uuid, result);
    }

    @DisplayName("Should reset user password")
    @Test
    void shouldResetUserPassword() {
        doNothing().when(userServiceWebClient).resetAccountPassword("testuser", "newPassword");

        service.resetUserPassword("testuser", "newPassword");
        verify(userServiceWebClient).resetAccountPassword("testuser", "newPassword");
    }

    @DisplayName("Should save code email recovery password")
    @Test
    void shouldSaveCodeEmailRecoveryPassword() {
        RegisteredUserData user = RegisteredUserData.builder().username("testuser").build();
        RequestedBrowserParams params = RequestedBrowserParams.builder().ipAddress("127.0.0.1").userAgent("Mozilla").build();
        RecoveryPasswordCodeRequest expected = RecoveryPasswordCodeRequest.builder().code("12345678901").username("testuser").build();

        when(userServiceWebClient.saveRecoveryPasswordCode(any(AccountRecoveryPasswordCodeRequestData.class))).thenReturn(expected);

        RecoveryPasswordCodeRequest result = service.saveCodeEmailRecoveryPassword(user, "12345678901", params);
        assertNotNull(result);
        assertEquals("12345678901", result.getCode());
    }

    @DisplayName("Should find code email recovery password")
    @Test
    void shouldFindCodeEmailRecoveryPassword() {
        RequestedBrowserParams params = RequestedBrowserParams.builder().ipAddress("127.0.0.1").userAgent("Mozilla").build();
        RecoveryPasswordCodeRequest expected = RecoveryPasswordCodeRequest.builder().code("12345678901").build();

        when(userServiceWebClient.findRecoveryPasswordCode("12345678901", "127.0.0.1", "Mozilla")).thenReturn(expected);

        RecoveryPasswordCodeRequest result = service.findCodeEmailRecoveryPassword("12345678901", params);
        assertNotNull(result);
        assertEquals("12345678901", result.getCode());
    }

    @DisplayName("Should save user via web client")
    @Test
    void shouldSaveUser() {
        UserRegistrationData regData = UserRegistrationData.builder()
                .username("webuser")
                .email("web@example.com")
                .firstName("Web")
                .lastName("User")
                .password("Password123")
                .build();

        AccountResponseData accountResponse = AccountResponseData.builder()
                .username("webuser")
                .email("web@example.com")
                .firstName("Web")
                .lastName("User")
                .enabled(true)
                .build();

        doNothing().when(userServiceWebClient).createAccount(any(AccountCreateRequestData.class));
        when(userServiceWebClient.getAccountData("webuser")).thenReturn(accountResponse);

        RegisteredUserData saved = service.saveUser(regData, UserRolesEnum.USER);
        assertNotNull(saved);
        assertEquals("webuser", saved.getUsername());
        assertEquals("web@example.com", saved.getEmail());
    }

    @DisplayName("Should get user by email via web client")
    @Test
    void shouldGetUserByEmail() {
        AccountResponseData accountResponse = AccountResponseData.builder()
                .username("webuser")
                .email("web@example.com")
                .firstName("Web")
                .lastName("User")
                .enabled(true)
                .build();

        when(userServiceWebClient.getAccountDataByEmail("web@example.com")).thenReturn(accountResponse);

        RegisteredUserData data = service.getUserByEmail("web@example.com");
        assertNotNull(data);
        assertEquals("webuser", data.getUsername());
        assertEquals("web@example.com", data.getEmail());
    }

    @DisplayName("Should verify user account via web client")
    @Test
    void shouldVerifyUserAccount() {
        when(userServiceWebClient.verifyAccount("uuid-code")).thenReturn(true);

        Boolean result = service.verifyUserAccount("uuid-code", RequestedBrowserParams.builder().build());
        assertTrue(result);
    }
}
