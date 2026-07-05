package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.impl;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.constants.UserRolesEnum;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.RecoveryPasswordCodeRequest;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.RegisteredUserData;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.RequestedBrowserParams;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.UserOperationsService;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.clients.entities.AccountCreateRequestData;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.clients.entities.AccountResponseData;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.clients.web.UserServiceWebClient;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.web.data.models.UserRegistrationData;

import java.util.Set;
import java.util.UUID;

public class WebClientUserOperationsServiceImpl implements UserOperationsService {
    private final UserServiceWebClient userServiceWebClient;

    public WebClientUserOperationsServiceImpl(UserServiceWebClient userServiceWebClient) {
        this.userServiceWebClient = userServiceWebClient;
    }

    @Override
    public Boolean checkIfHasEmailUsedByAnotherUser(String email) {
        return userServiceWebClient.accountExists(email);
    }

    @Override
    public Boolean checkIfHasUsernameUsedByAnotherUser(String username) {
        return userServiceWebClient.accountExists(username);
    }

    @Override
    public UUID generateUUIDCodeToCheckAccountMailVerification(RegisteredUserData client) {
        return UUID.randomUUID();
    }

    @Override
    public void resetUserPassword(String username, String newPassword) {
        userServiceWebClient.resetAccountPassword(username, newPassword);
    }

    @Override
    public RecoveryPasswordCodeRequest saveCodeEmailRecoveryPassword(RegisteredUserData client, String code, RequestedBrowserParams requestedBrowserParams) {
        return null;
    }

    @Override
    public RecoveryPasswordCodeRequest findCodeEmailRecoveryPassword(String code, RequestedBrowserParams requestedBrowserParams) {
        return null;
    }

    @Override
    public RegisteredUserData saveUser(UserRegistrationData userRegistrationData, UserRolesEnum role) {
        userServiceWebClient.createAccount(AccountCreateRequestData.builder()
                        .roles(Set.of(role.name()))
                        .email(userRegistrationData.getEmail())
                        .lastName(userRegistrationData.getLastName())
                        .firstName(userRegistrationData.getFirstName())
                        .password(userRegistrationData.getPassword())
                        .username(userRegistrationData.getUsername())
                        .build());

        return getUserByUsername(userRegistrationData.getUsername());
    }

    @Override
    public RegisteredUserData getUserByUsername(String username) {
        AccountResponseData accountData = userServiceWebClient.getAccountData(username);
        return RegisteredUserData.builder()
                .username(accountData.getUsername())
                .isEnabled(accountData.getEnabled())
                .lastName(accountData.getLastName())
                .firstName(accountData.getFirstName())
                .build();
    }

    @Override
    public RegisteredUserData getUserByEmail(String email) {
        AccountResponseData accountData = userServiceWebClient.getAccountData(email);
        return RegisteredUserData.builder()
                .username(accountData.getUsername())
                .isEnabled(accountData.getEnabled())
                .lastName(accountData.getLastName())
                .firstName(accountData.getFirstName())
                .build();
    }

    @Override
    public Boolean verifyUserAccount(String uuidCode, RequestedBrowserParams browserParams) {
        return null;
    }
}
