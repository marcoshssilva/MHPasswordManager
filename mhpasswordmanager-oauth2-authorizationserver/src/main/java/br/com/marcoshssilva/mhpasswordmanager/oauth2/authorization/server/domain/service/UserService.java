package br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.domain.service;

import br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.domain.constants.UserRolesEnum;
import br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.web.data.models.UserRegistrationData;

public interface UserService {
    void registerNewUser(UserRegistrationData userRegistrationData, UserRolesEnum role);
    Boolean isUserExistsAccount(String email);
    Boolean isAuthenticated();
}
