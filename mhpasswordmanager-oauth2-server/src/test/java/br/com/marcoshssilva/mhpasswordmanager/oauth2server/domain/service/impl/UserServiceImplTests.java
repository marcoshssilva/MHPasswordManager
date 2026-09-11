package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.impl;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.configuration.AuthorizationConfigProperties;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.constants.UserRolesEnum;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.exceptions.BusinessRuleException;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.exceptions.FailSendEmailException;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.RecoveryPasswordCodeRequest;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.RegisteredUserCheckMailVerificationMessage;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.RegisteredUserData;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.RegisteredUserKeyVerificationMailMessage;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.RequestedBrowserParams;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.SendEmailService;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.UserOperationsService;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.web.data.models.UserRegistrationData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTests {

    @Mock
    private UserOperationsService userOperationsService;

    @Mock
    private SendEmailService sendEmailService;

    private AuthorizationConfigProperties authorizationConfigProperties;
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        authorizationConfigProperties = new AuthorizationConfigProperties();
        authorizationConfigProperties.setIssuerUri("http://localhost:12010");
        userService = new UserServiceImpl(userOperationsService, sendEmailService, authorizationConfigProperties);
    }

    @DisplayName("Should register new user successfully and send verification email")
    @Test
    void shouldRegisterNewUserSuccessfully() throws Exception {
        UserRegistrationData regData = UserRegistrationData.builder()
                .username("testuser")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .password("Password123")
                .confirmationPassword("Password123")
                .build();

        RegisteredUserData savedUser = RegisteredUserData.builder()
                .username("testuser")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .isEnabled(false)
                .build();

        UUID verificationUuid = UUID.randomUUID();

        when(userOperationsService.checkIfHasEmailUsedByAnotherUser("test@example.com")).thenReturn(false);
        when(userOperationsService.checkIfHasUsernameUsedByAnotherUser("testuser")).thenReturn(false);
        when(userOperationsService.saveUser(regData, UserRolesEnum.USER)).thenReturn(savedUser);
        when(userOperationsService.generateUUIDCodeToCheckAccountMailVerification(savedUser)).thenReturn(verificationUuid);
        doNothing().when(sendEmailService).sendEmailVerifyAccount(any(RegisteredUserCheckMailVerificationMessage.class));

        assertDoesNotThrow(() -> userService.registerNewUser(regData, UserRolesEnum.USER));

        ArgumentCaptor<RegisteredUserCheckMailVerificationMessage> msgCaptor = ArgumentCaptor.forClass(RegisteredUserCheckMailVerificationMessage.class);
        verify(sendEmailService).sendEmailVerifyAccount(msgCaptor.capture());
        assertEquals("test@example.com", msgCaptor.getValue().getEmail());
        assertEquals("Test", msgCaptor.getValue().getName());
        assertEquals("http://localhost:12010/verify/" + verificationUuid, msgCaptor.getValue().getLink());
    }

    @DisplayName("Should throw BusinessRuleException when email is already in use")
    @Test
    void shouldThrowExceptionWhenEmailAlreadyInUse() {
        UserRegistrationData regData = UserRegistrationData.builder()
                .username("testuser")
                .email("test@example.com")
                .build();

        when(userOperationsService.checkIfHasEmailUsedByAnotherUser("test@example.com")).thenReturn(true);

        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> userService.registerNewUser(regData, UserRolesEnum.USER));
        assertEquals("Cannot use this username/email.", ex.getMessage());
    }

    @DisplayName("Should throw BusinessRuleException when username is already in use")
    @Test
    void shouldThrowExceptionWhenUsernameAlreadyInUse() {
        UserRegistrationData regData = UserRegistrationData.builder()
                .username("testuser")
                .email("test@example.com")
                .build();

        when(userOperationsService.checkIfHasEmailUsedByAnotherUser("test@example.com")).thenReturn(false);
        when(userOperationsService.checkIfHasUsernameUsedByAnotherUser("testuser")).thenReturn(true);

        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> userService.registerNewUser(regData, UserRolesEnum.USER));
        assertEquals("Cannot use this username/email.", ex.getMessage());
    }

    @DisplayName("Should not fail registration if sendEmailVerifyAccount throws FailSendEmailException")
    @Test
    void shouldNotFailRegistrationWhenEmailSendingFails() throws Exception {
        UserRegistrationData regData = UserRegistrationData.builder()
                .username("testuser")
                .email("test@example.com")
                .firstName("Test")
                .build();

        RegisteredUserData savedUser = RegisteredUserData.builder()
                .username("testuser")
                .email("test@example.com")
                .firstName("Test")
                .build();

        UUID verificationUuid = UUID.randomUUID();

        when(userOperationsService.checkIfHasEmailUsedByAnotherUser("test@example.com")).thenReturn(false);
        when(userOperationsService.checkIfHasUsernameUsedByAnotherUser("testuser")).thenReturn(false);
        when(userOperationsService.saveUser(regData, UserRolesEnum.USER)).thenReturn(savedUser);
        when(userOperationsService.generateUUIDCodeToCheckAccountMailVerification(savedUser)).thenReturn(verificationUuid);
        doThrow(new FailSendEmailException("SMTP error")).when(sendEmailService).sendEmailVerifyAccount(any());

        assertDoesNotThrow(() -> userService.registerNewUser(regData, UserRolesEnum.USER));
    }

    @DisplayName("Should generate and send confirmation code to reset password successfully")
    @Test
    void shouldGenerateAndSendConfirmationCodeToResetPasswordSuccessfully() throws Exception {
        RequestedBrowserParams browserParams = RequestedBrowserParams.builder().ipAddress("127.0.0.1").userAgent("Mozilla").build();
        RegisteredUserData userData = RegisteredUserData.builder()
                .username("testuser")
                .email("test@example.com")
                .firstName("Test")
                .build();

        when(userOperationsService.getUserByEmail("test@example.com")).thenReturn(userData);
        when(userOperationsService.saveCodeEmailRecoveryPassword(eq(userData), any(), eq(browserParams))).thenReturn(RecoveryPasswordCodeRequest.builder().build());
        doNothing().when(sendEmailService).sendEmailRecoveryPassword(any(RegisteredUserKeyVerificationMailMessage.class));

        assertDoesNotThrow(() -> userService.generateAndSendConfirmationCodeToResetPassword("test@example.com", browserParams));

        verify(userOperationsService).saveCodeEmailRecoveryPassword(eq(userData), any(), eq(browserParams));
        verify(sendEmailService).sendEmailRecoveryPassword(any(RegisteredUserKeyVerificationMailMessage.class));
    }

    @DisplayName("Should throw BusinessRuleException when user does not exist on password reset")
    @Test
    void shouldThrowExceptionWhenUserDoesNotExistOnPasswordReset() {
        RequestedBrowserParams browserParams = RequestedBrowserParams.builder().build();
        when(userOperationsService.getUserByEmail("notfound@example.com")).thenReturn(null);

        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> userService.generateAndSendConfirmationCodeToResetPassword("notfound@example.com", browserParams));
        assertEquals("Cannot send email, user doesn't have registered account.", ex.getMessage());
    }

    @DisplayName("Should not throw when sending email fails on password reset code generation")
    @Test
    void shouldNotThrowWhenEmailFailsOnPasswordReset() throws Exception {
        RequestedBrowserParams browserParams = RequestedBrowserParams.builder().build();
        RegisteredUserData userData = RegisteredUserData.builder()
                .username("testuser")
                .email("test@example.com")
                .firstName("Test")
                .build();

        when(userOperationsService.getUserByEmail("test@example.com")).thenReturn(userData);
        when(userOperationsService.saveCodeEmailRecoveryPassword(eq(userData), any(), eq(browserParams))).thenReturn(RecoveryPasswordCodeRequest.builder().build());
        doThrow(new FailSendEmailException("SMTP error")).when(sendEmailService).sendEmailRecoveryPassword(any());

        assertDoesNotThrow(() -> userService.generateAndSendConfirmationCodeToResetPassword("test@example.com", browserParams));
    }

    @DisplayName("Should reset password from recovery password code request successfully")
    @Test
    void shouldResetPasswordFromRecoveryPasswordCodeRequestSuccessfully() throws Exception {
        RequestedBrowserParams browserParams = RequestedBrowserParams.builder().build();
        RecoveryPasswordCodeRequest codeRequest = RecoveryPasswordCodeRequest.builder()
                .code("12345678901")
                .username("testuser")
                .build();

        when(userOperationsService.findCodeEmailRecoveryPassword("12345678901", browserParams)).thenReturn(codeRequest);
        doNothing().when(userOperationsService).resetUserPassword("testuser", "NewPassword123");

        assertDoesNotThrow(() -> userService.resetPasswordFromRecoveryPasswordCodeRequest("12345678901", "NewPassword123", browserParams));

        verify(userOperationsService).resetUserPassword("testuser", "NewPassword123");
    }

    @DisplayName("Should throw BusinessRuleException when recovery password codeRequest is null")
    @Test
    void shouldThrowExceptionWhenCodeRequestIsNull() {
        RequestedBrowserParams browserParams = RequestedBrowserParams.builder().build();
        when(userOperationsService.findCodeEmailRecoveryPassword("invalid-code", browserParams)).thenReturn(null);

        assertThrows(BusinessRuleException.class,
                () -> userService.resetPasswordFromRecoveryPasswordCodeRequest("invalid-code", "NewPassword123", browserParams));
    }

    @DisplayName("Should throw BusinessRuleException when username is null in codeRequest")
    @Test
    void shouldThrowExceptionWhenUsernameIsNullInCodeRequest() {
        RequestedBrowserParams browserParams = RequestedBrowserParams.builder().build();
        RecoveryPasswordCodeRequest codeRequest = RecoveryPasswordCodeRequest.builder()
                .code("12345678901")
                .username(null)
                .build();

        when(userOperationsService.findCodeEmailRecoveryPassword("12345678901", browserParams)).thenReturn(codeRequest);

        assertThrows(BusinessRuleException.class,
                () -> userService.resetPasswordFromRecoveryPasswordCodeRequest("12345678901", "NewPassword123", browserParams));
    }

    @DisplayName("Should verify user account successfully")
    @Test
    void shouldVerifyUserAccountSuccessfully() throws Exception {
        RequestedBrowserParams browserParams = RequestedBrowserParams.builder().build();
        when(userOperationsService.verifyUserAccount("uuid-code", browserParams)).thenReturn(true);

        assertDoesNotThrow(() -> userService.verifyUserAccount("uuid-code", browserParams));
        verify(userOperationsService).verifyUserAccount("uuid-code", browserParams);
    }

    @DisplayName("Should throw BusinessRuleException when user verification fails")
    @Test
    void shouldThrowExceptionWhenUserVerificationFails() {
        RequestedBrowserParams browserParams = RequestedBrowserParams.builder().build();
        doThrow(new RuntimeException("DB error")).when(userOperationsService).verifyUserAccount("invalid-code", browserParams);

        assertThrows(BusinessRuleException.class, () -> userService.verifyUserAccount("invalid-code", browserParams));
    }
}
