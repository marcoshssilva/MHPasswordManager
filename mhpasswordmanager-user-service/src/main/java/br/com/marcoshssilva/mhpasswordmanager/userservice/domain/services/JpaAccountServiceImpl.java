package br.com.marcoshssilva.mhpasswordmanager.userservice.domain.services;

import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.entities.Account;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.entities.AccountDetails;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.repositories.AccountDetailsRepository;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.repositories.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
public class JpaAccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountDetailsRepository accountDetailsRepository;

    @Override
    public Page<Account> getAllUsers(Pageable pageable) {
        return accountRepository.findAll(pageable);
    }

    @Override
    public Page<Account> getAllUsersByRole(String role, Pageable pageable) {
        HashSet<String> set = new HashSet<>();
        final String prefix = "ROLE";
        final String separator = "_";

        set.add(String.join(separator, prefix, role));
        return accountRepository.getAccountsByRolesIn(set, pageable);
    }

    @Override
    public Account getUserByUsername(String email) {
        return accountRepository.findById(email).orElseThrow();
    }

    @Override
    @Transactional(rollbackFor = NoSuchElementException.class)
    public void saveDetails(AccountDetails accountDetails) {
        accountRepository.findById(accountDetails.getUsername()).orElseThrow();
        accountDetailsRepository.save(accountDetails);
    }
}
