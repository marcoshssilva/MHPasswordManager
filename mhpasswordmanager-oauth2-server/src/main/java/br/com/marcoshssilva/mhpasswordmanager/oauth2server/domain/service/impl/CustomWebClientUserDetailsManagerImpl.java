package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.impl;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.clients.entities.AccountCreateRequestData;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.clients.entities.AccountUserInternalResponseData;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.clients.web.UserServiceWebClient;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;

@RequiredArgsConstructor
public class CustomWebClientUserDetailsManagerImpl implements UserDetailsManager {
    private final UserServiceWebClient userServiceWebClient;

    @Override
    public void createUser(UserDetails user) {
        userServiceWebClient.createAccount(AccountCreateRequestData.from(user));
    }

    @Override
    public void updateUser(UserDetails user) {
        assertUserExists(user.getUsername());
        updateEnabled(user.getUsername(), user.isEnabled());
        if (user.getPassword() != null) {
            resetPassword(user.getUsername(), user.getPassword());
        }
    }

    @Override
    public void deleteUser(String username) {
        userServiceWebClient.deleteAccount(username);
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new UsernameNotFoundException("Cannot change password without authenticated user.");
        }
        validatePassword(authentication.getName(), oldPassword);
        resetPassword(authentication.getName(), newPassword);
    }

    @Override
    public boolean userExists(String username) {
        return userServiceWebClient.getInternalUserFromAccount(username) != null;
    }

    public boolean validatePassword(String username, String password) {
        return userServiceWebClient.validatePassword(username, password);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AccountUserInternalResponseData account = userServiceWebClient.getInternalUserFromAccount(username);
        if (account == null) {
            throw new UsernameNotFoundException(username);
        }
        return User.withUsername(account.getUsername())
                .password(account.getPassword())
                .disabled(Boolean.FALSE.equals(account.getEnabled()))
                .authorities(account.getRoles().toArray(new String[0]))
                .build();
    }

    private void assertUserExists(String username) {
        if (!userExists(username)) {
            throw new UsernameNotFoundException(username);
        }
    }

    private void updateEnabled(String username, Boolean enabled) {
        userServiceWebClient.updateAccountEnabled(username, enabled);
    }

    private void resetPassword(String username, String newPassword) {
        userServiceWebClient.resetAccountPassword(username, newPassword);
    }
}
