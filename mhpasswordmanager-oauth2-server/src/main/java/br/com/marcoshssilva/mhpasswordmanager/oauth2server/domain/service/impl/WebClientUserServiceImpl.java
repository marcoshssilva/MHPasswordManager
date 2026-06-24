package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.impl;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.exceptions.BusinessRuleException;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.exceptions.FailSendEmailException;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.RequestedBrowserParams;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.AbstractUserService;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.UserService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;

public class WebClientUserServiceImpl extends AbstractUserService implements UserService {
    public WebClientUserServiceImpl(UserDetailsManager userDetailsManager, PasswordEncoder passwordEncoder) {
        super(userDetailsManager, passwordEncoder);
    }

    @Override
    public void generateAndSendConfirmationCodeToResetPassword(String email, RequestedBrowserParams requestedBrowserParams) throws FailSendEmailException {
        throw new UnsupportedOperationException("Password recovery by web client is not implemented yet.");
    }

    @Override
    public void resetPasswordFromRecoveryPasswordCodeRequest(String code, String newPassword, RequestedBrowserParams requestedBrowserParams) throws BusinessRuleException {
        throw new UnsupportedOperationException("Password reset by recovery code over web client is not implemented yet.");
    }

    @Override
    public void resetUserPassword(String username, String newPassword) throws BusinessRuleException {
        throw new UnsupportedOperationException("Password reset over web client is not implemented yet.");
    }

    @Override
    public void verifyUserAccount(String uuidCode, RequestedBrowserParams browserParams) throws BusinessRuleException {
        throw new UnsupportedOperationException("Account verification over web client is not implemented yet.");
    }
}
