package br.com.marcoshssilva.mhpasswordmanager.userservice.domain.services;

import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.entities.Account;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.entities.AccountDetails;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

@Service
public interface AccountService {
    Page<Account> getAllUsers(Pageable pageable);
    Page<Account> getAllUsersByRole(String role, Pageable pageable);
    Account getUserByUsername(String email);
    void saveDetails(AccountDetails accountDetails);
}
