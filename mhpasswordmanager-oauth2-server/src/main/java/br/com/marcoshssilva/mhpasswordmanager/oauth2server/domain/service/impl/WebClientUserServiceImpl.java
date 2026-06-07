package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.impl;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.exceptions.BusinessRuleException;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.exceptions.FailSendEmailException;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.RequestedBrowserParams;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.AbstractUserService;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.UserService;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.clients.web.UserServiceWebClient;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;

public class WebClientUserServiceImpl extends AbstractUserService implements UserService {
    private final UserServiceWebClient userServiceWebClient;

    public WebClientUserServiceImpl(UserDetailsManager userDetailsManager, PasswordEncoder passwordEncoder, UserServiceWebClient userServiceWebClient) {
        super(userDetailsManager, passwordEncoder);
        this.userServiceWebClient = userServiceWebClient;
    }

    @Override
    public void generateAndSendConfirmationCodeToResetPassword(String email, RequestedBrowserParams requestedBrowserParams) throws FailSendEmailException {

    }

    @Override
    public void resetPasswordFromRecoveryPasswordCodeRequest(String code, String newPassword, RequestedBrowserParams requestedBrowserParams) throws BusinessRuleException {

    }

    @Override
    public void resetUserPassword(String username, String newPassword) throws BusinessRuleException {

    }

    @Override
    public void verifyUserAccount(String uuidCode, RequestedBrowserParams browserParams) throws BusinessRuleException {

    }
}
