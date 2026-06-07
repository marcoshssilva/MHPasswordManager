package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.constants.UserRolesEnum;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.exceptions.CannotRegisterUserException;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.web.data.models.UserRegistrationData;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;

public abstract class AbstractUserService implements UserService {
    protected static final String ERROR_MESSAGE_CANNOT_USE = "Cannot use this username/email.";
    private final UserDetailsManager userDetailsManager;
    private final PasswordEncoder passwordEncoder;

    protected AbstractUserService(UserDetailsManager userDetailsManager, PasswordEncoder passwordEncoder) {
        this.userDetailsManager = userDetailsManager;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void registerNewUser(UserRegistrationData userRegistrationData, UserRolesEnum role) {

        // Call UserDetailsManager to delegate check if the user already exists
        if (userDetailsManager.userExists(userRegistrationData.getUsername())) {
            throw new CannotRegisterUserException(ERROR_MESSAGE_CANNOT_USE);
        }

        // Call UserDetailsManager to delegate the creation of the user
        userDetailsManager.createUser(User.withUsername(userRegistrationData.getUsername()).password(passwordEncoder.encode(userRegistrationData.getPassword())).roles(role.name()).build());

    }

    @Override
    public Boolean isUserExistsAccount(String username) {
        return userDetailsManager.userExists(username);
    }

    protected UserDetailsManager getUserDetailsManager() {
        return userDetailsManager;
    }

    protected PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }
}
