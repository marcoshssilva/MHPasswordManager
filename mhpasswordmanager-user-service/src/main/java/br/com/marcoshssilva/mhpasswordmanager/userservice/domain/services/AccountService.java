package br.com.marcoshssilva.mhpasswordmanager.userservice.domain.services;

import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.exceptions.AlreadyExistsInDatabaseException;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.exceptions.ElementNotFoundException;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.models.AccountDataModel;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.models.AccountDataToUpdateModel;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.models.AccountRegistrationModel;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.models.AccountRecoveryPasswordCodeModel;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

@Service
public interface AccountService {
    Page<AccountDataModel> getAllUsers(Pageable pageable);
    AccountDataModel getUserByUsername(String username) throws ElementNotFoundException;
    AccountDataModel getUserByEmail(String email) throws ElementNotFoundException;
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    AccountDataModel register(AccountRegistrationModel account) throws AlreadyExistsInDatabaseException;
    void updateAccountDetailsByUsername(String username, AccountDataToUpdateModel account) throws ElementNotFoundException;
    void updatePasswordByUsername(String username, String pass) throws ElementNotFoundException;
    void updateAccountHasEnabledByUsername(String username, Boolean isEnabled) throws ElementNotFoundException;
    void deleteAccountByUsername(String username) throws ElementNotFoundException;
    Boolean matchPasswordFromUsername(String username, String pass) throws ElementNotFoundException;
    UUID generateAccountVerificationCode(String username) throws ElementNotFoundException;
    Boolean verifyAccount(String uuidCode) throws ElementNotFoundException;
    AccountRecoveryPasswordCodeModel saveRecoveryPasswordCode(String username, String code, String ipClient, String userAgentClient) throws ElementNotFoundException;
    AccountRecoveryPasswordCodeModel findRecoveryPasswordCode(String code, String ipClient, String userAgentClient) throws ElementNotFoundException;
}
