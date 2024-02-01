package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.exceptions.UserAuthorizationCannotBeLoadedException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.exceptions.UserRegistrationNotFoundException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.models.UserRegisteredModel;
import org.springframework.security.oauth2.jwt.Jwt;

public interface UserRegistrationService {
    UserRegisteredModel getUserRegistration(String email) throws UserRegistrationNotFoundException;
    UserAuthorizations getUserAuthorizations(Jwt jwt) throws UserAuthorizationCannotBeLoadedException;
}
