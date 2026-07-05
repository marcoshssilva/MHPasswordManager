package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.constants.UserRolesEnum;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.RecoveryPasswordCodeRequest;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.RegisteredUserData;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.RequestedBrowserParams;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.web.data.models.UserRegistrationData;

import java.util.UUID;

public interface UserOperationsService {
    Boolean checkIfHasEmailUsedByAnotherUser(String email);
    Boolean checkIfHasUsernameUsedByAnotherUser(String username);
    UUID generateUUIDCodeToCheckAccountMailVerification(RegisteredUserData client);
    void resetUserPassword(String username, String newPassword);
    RecoveryPasswordCodeRequest saveCodeEmailRecoveryPassword(RegisteredUserData client, String code, RequestedBrowserParams requestedBrowserParams);
    RecoveryPasswordCodeRequest findCodeEmailRecoveryPassword(String code, RequestedBrowserParams requestedBrowserParams);
    RegisteredUserData saveUser(UserRegistrationData userRegistrationData, UserRolesEnum role);
    RegisteredUserData getUserByUsername(String username);
    RegisteredUserData getUserByEmail(String email);
    Boolean verifyUserAccount(String uuidCode, RequestedBrowserParams browserParams);
}
