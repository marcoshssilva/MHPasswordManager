package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.impl;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.configuration.AuthorizationConfigProperties;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.constants.UserRolesEnum;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.exceptions.BusinessRuleException;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.exceptions.CannotRegisterUserException;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.exceptions.FailSendEmailException;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.*;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.SendEmailService;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.UserOperationsService;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.UserService;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.utils.KeyGeneratorUtils;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.web.data.models.UserRegistrationData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class UserServiceImpl implements UserService {
    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);
    private static final String ERROR_MESSAGE_CANNOT_USE = "Cannot use this username/email.";
    private static final String ERROR_MESSAGE_CANNOT_SEND_LINK_CHECK_EMAIL = "Cannot send link to check email.";
    private static final String ERROR_MESSAGE_CANNOT_SEND_RESET_PASSWORD_EMAIL = "Cannot send email to user to reset password.";
    private final UserOperationsService userOperationsService;
    private final SendEmailService sendEmailService;
    private final AuthorizationConfigProperties authorizationConfigProperties;

    public UserServiceImpl(UserOperationsService userOperationsService, SendEmailService sendEmailService, AuthorizationConfigProperties authorizationConfigProperties) {
        this.userOperationsService = userOperationsService;
        this.sendEmailService = sendEmailService;
        this.authorizationConfigProperties = authorizationConfigProperties;
    }

    @Override
    public void registerNewUser(UserRegistrationData userRegistrationData, UserRolesEnum role) throws BusinessRuleException {
        try {
            // check if email already used by another account
            if (userOperationsService.checkIfHasEmailUsedByAnotherUser(userRegistrationData.getEmail())) {
                throw new CannotRegisterUserException(ERROR_MESSAGE_CANNOT_USE);
            }
            // check if the user already exists
            if (userOperationsService.checkIfHasUsernameUsedByAnotherUser(userRegistrationData.getUsername())) {
                throw new CannotRegisterUserException(ERROR_MESSAGE_CANNOT_USE);
            }

            // call UserOperationsService to save user
            RegisteredUserData userData = userOperationsService.saveUser(userRegistrationData, role);

            // send email to user with link to verify account
            try {
                UUID uuidVerifyAccount = userOperationsService.generateUUIDCodeToCheckAccountMailVerification(userData);
                sendEmailService.sendEmailVerifyAccount(RegisteredUserCheckMailVerificationMessage.builder().name(userData.getFirstName()).email(userData.getEmail()).link(authorizationConfigProperties.getIssuerUri().concat("/verify/").concat(uuidVerifyAccount.toString())).build());
            } catch (FailSendEmailException e) {
                LOG.error(ERROR_MESSAGE_CANNOT_SEND_LINK_CHECK_EMAIL, e);
            }
        } catch (Exception e) {
            throw new BusinessRuleException(e.getMessage(), e);
        }
    }

    @Override
    public void generateAndSendConfirmationCodeToResetPassword(String email, RequestedBrowserParams requestedBrowserParams) throws BusinessRuleException {
        try {
            // check if email or username is registered
            RegisteredUserData registeredUserData = userOperationsService.getUserByEmail(email);
            if (registeredUserData == null) {
                throw new BusinessRuleException("Cannot send email, user doesn't have registered account.");
            }
            // generate code
            String code = KeyGeneratorUtils.generateRecoveryCodeToResetPassword();
            // save and send by mail-service queue
            try {
                userOperationsService.saveCodeEmailRecoveryPassword(registeredUserData, code, requestedBrowserParams);
                sendEmailService.sendEmailRecoveryPassword(RegisteredUserKeyVerificationMailMessage.builder()
                        .code(code)
                        .email(registeredUserData.getEmail())
                        .name(registeredUserData.getFirstName())
                        .build());
            } catch (FailSendEmailException e) {
                LOG.error(ERROR_MESSAGE_CANNOT_SEND_RESET_PASSWORD_EMAIL, e);
            }
        } catch (Exception e) {
            throw new BusinessRuleException(e.getMessage(), e);
        }
    }

    @Override
    public void resetPasswordFromRecoveryPasswordCodeRequest(String code, String newPassword, RequestedBrowserParams requestedBrowserParams) throws BusinessRuleException {
        try {
            RecoveryPasswordCodeRequest codeRequest = userOperationsService.findCodeEmailRecoveryPassword(code, requestedBrowserParams);
            if (codeRequest == null) {
                throw new BusinessRuleException("Cannot reset password, missing codeRequest.");
            }
            if (codeRequest.getUsername() == null) {
                throw new BusinessRuleException("Cannot reset password, missing username.");
            }
            this.userOperationsService.resetUserPassword(codeRequest.getUsername(), newPassword);
        } catch (Exception e) {
            throw new BusinessRuleException("Cannot reset password, please try again later.", e);
        }
    }

    @Override
    public void verifyUserAccount(String uuidCode, RequestedBrowserParams browserParams) throws BusinessRuleException {
        try {
            this.userOperationsService.verifyUserAccount(uuidCode, browserParams);
        } catch (Exception e) {
            throw new BusinessRuleException("Cannot verify user account, please check params.", e);
        }
    }
}
