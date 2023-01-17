package br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.domain.service;

import br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.domain.constants.UserRolesEnum;
import br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.domain.data.models.UserRegistrationData;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Create a new account in this system
     *
     * @param userRegistrationData -> Model for user data
     * @param role -> Role for use granted authorities
     */
    @Override
    @Transactional
    public void registerNewUser(@Valid UserRegistrationData userRegistrationData, UserRolesEnum role) {
        getUserDetailsManager().createUser(
                User.builder()
                        .username(userRegistrationData.getEmail())
                        .password(passwordEncoder.encode(userRegistrationData.getPassword()))
                        .roles(role.name())
                        .build());
    }

    /**
     * Verify if an account with this email exists
     *
     * @param email -> email from registered user
     * @return True if exists
     */
    @Override
    public Boolean isUserExistsAccount(String email) {
        return getUserDetailsManager().userExists(email);
    }

    @Override
    public Boolean isAuthenticated() {
        return SecurityContextHolder.getContext().getAuthentication().isAuthenticated();
    }

    private UserDetailsManager getUserDetailsManager() {
        return (UserDetailsManager) this.userDetailsService;
    }

}
