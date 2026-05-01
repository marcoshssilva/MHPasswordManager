package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.constants.UserRolesEnum;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.exceptions.BusinessRuleException;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.exceptions.FailSendEmailException;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.RequestedBrowserParams;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.web.data.models.UserRegistrationData;
import org.springframework.security.core.context.SecurityContextHolder;

public interface UserService {
    /**
     * Create a new account in this system
     *
     * @param userRegistrationData -> Model for user data
     * @param role                 -> Role for use granted authorities
     */
    void registerNewUser(UserRegistrationData userRegistrationData, UserRolesEnum role);
    /**
     * Verify if an account with this email exists
     *
     * @param email -> email from registered user
     * @return True if exists
     */
    Boolean isUserExistsAccount(String email);

    /**
     * Generate code to reset password and send by email from requested registrated user
     * @param email
     * @throws FailSendEmailException
     */
    void generateAndSendConfirmationCodeToResetPassword(String email, RequestedBrowserParams requestedBrowserParams) throws FailSendEmailException;

    /**
     * Reset password for user with email and code generated in last step
     * @param code -> code generated in last step
     * @param newPassword -> new password for user
     * @param requestedBrowserParams -> browser params from request
     * @throws BusinessRuleException
     */
    void resetPasswordFromRecoveryPasswordCodeRequest(String code, String newPassword, RequestedBrowserParams requestedBrowserParams) throws BusinessRuleException;

    void resetUserPassword(String username, String newPassword) throws BusinessRuleException;

    /**
     * Verify if current user is logged into application
     *
     * @return True if is logged
     */
    default Boolean isAuthenticated()  {
        return SecurityContextHolder.getContext().getAuthentication().isAuthenticated();
    }
}
