package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.constants.UserRolesEnum;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.exceptions.BusinessRuleException;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.RequestedBrowserParams;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.web.data.models.UserRegistrationData;
import org.springframework.security.core.context.SecurityContextHolder;

public interface UserService {
    void registerNewUser(UserRegistrationData userRegistrationData, UserRolesEnum role) throws BusinessRuleException;

    void generateAndSendConfirmationCodeToResetPassword(String email, RequestedBrowserParams requestedBrowserParams)
            throws BusinessRuleException;

    void resetPasswordFromRecoveryPasswordCodeRequest(String code, String newPassword,
            RequestedBrowserParams requestedBrowserParams) throws BusinessRuleException;

    void verifyUserAccount(String uuidCode, RequestedBrowserParams browserParams) throws BusinessRuleException;

    default Boolean isAuthenticated() {
        return SecurityContextHolder.getContext().getAuthentication().isAuthenticated();
    }
}
