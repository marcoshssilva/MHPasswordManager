package br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.domain.service;

import br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.domain.constants.UserRolesEnum;
import br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.web.data.models.UserRegistrationData;
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
     * Verify if current user is logged into application
     *
     * @return True if is logged
     */
    default Boolean isAuthenticated()  {
        return SecurityContextHolder.getContext().getAuthentication().isAuthenticated();
    };
}
