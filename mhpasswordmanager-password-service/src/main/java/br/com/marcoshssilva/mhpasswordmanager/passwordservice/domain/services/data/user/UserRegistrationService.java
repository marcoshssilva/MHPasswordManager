package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.models.NewUserRegisteredModel;

public interface UserRegistrationService {
    NewUserRegisteredModel createUserRegistration(String email, String vaultKey) throws UserRegistrationException, UserRegistrationAlreadyExistsException;
}
