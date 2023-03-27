package br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.domain.service.impl;

import br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.domain.constants.UserRolesEnum;
import br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.domain.service.UserService;
import br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.web.data.models.UserRegistrationData;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class HttpRequestUserServiceImpl implements UserService {
    @Override
    public void registerNewUser(UserRegistrationData userRegistrationData, UserRolesEnum role) {
        // do nothing
    }

    @Override
    public Boolean isUserExistsAccount(String email) {
        return true;
    }
}
