package br.com.marcoshssilva.mhpasswordmanager.userservice.domain.services.impl;

import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.entities.Account;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.entities.AccountDetails;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.enums.DefaultUserRoles;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.exceptions.AlreadyExistsInDatabaseException;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.exceptions.ElementNotFoundException;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.models.AccountDataModel;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.models.AccountDataToUpdateModel;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.models.AccountRegistrationModel;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.repositories.AccountDetailsRepository;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.repositories.AccountRepository;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.services.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class JpaAccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final AccountDetailsRepository accountDetailsRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Page<AccountDataModel> getAllUsers(Pageable pageable) {
        return accountRepository
                .findAll(pageable)
                .map(item -> new AccountDataModel(item.getUsername(), item.getPassword(), item.getEnabled(), item.getRoles(), item.getAccountDetails().getFirstName(), item.getAccountDetails().getLastName(), item.getAccountDetails().getImageUrl()));
    }

    @Override
    public AccountDataModel getUserByUsername(String email) throws ElementNotFoundException {
        Account account = accountRepository.findById(email).orElseThrow(ElementNotFoundException::new);
        return new AccountDataModel(account.getUsername(), account.getPassword(), account.getEnabled(),
                                    account.getRoles(), account.getAccountDetails().getFirstName(), account.getAccountDetails().getLastName(),
                                    account.getAccountDetails().getImageUrl());
    }

    @Override
    public AccountDataModel register(AccountRegistrationModel account) throws AlreadyExistsInDatabaseException {
        Optional<Account> accountInDb = accountRepository.findById(account.username());
        if (accountInDb.isPresent()) {
            throw new AlreadyExistsInDatabaseException();
        }

        Account accountToSave = new Account();
        accountToSave.setUsername(account.username());
        accountToSave.setPassword(passwordEncoder.encode(account.password()));
        accountToSave.setEnabled(account.enabled());
        accountToSave.setRoles(account.roles().stream().map(DefaultUserRoles::getValue).collect(Collectors.toSet()));

        AccountDetails accountDetailsToSave = new AccountDetails();
        accountDetailsToSave.setUsername(account.username());
        accountDetailsToSave.setFirstName(account.firstName());
        accountDetailsToSave.setLastName(account.lastName());

        accountToSave = accountRepository.save(accountToSave);
        accountDetailsToSave = accountDetailsRepository.save(accountDetailsToSave);

        return new AccountDataModel(accountToSave.getUsername(), accountToSave.getPassword(), accountToSave.getEnabled(),
                                    accountToSave.getRoles(),
                                    accountDetailsToSave.getFirstName(), accountDetailsToSave.getLastName(),
                                    accountDetailsToSave.getImageUrl());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAccountDetailsByUsername(String username, AccountDataToUpdateModel account) throws ElementNotFoundException {
        Account accountInDb = accountRepository.findById(username).orElseThrow(ElementNotFoundException::new);
        accountDetailsRepository.updateAccountDetailsByUsername(accountInDb.getUsername(), account.firstName(), account.lastName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePasswordByUsername(String username, String pass) throws ElementNotFoundException {
        Account account = accountRepository.findById(username).orElseThrow(ElementNotFoundException::new);
        this.accountRepository.updatePasswordByUsername(account.getUsername(), passwordEncoder.encode(pass));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAccountHasEnabledByUsername(String username, Boolean isEnabled) throws ElementNotFoundException {
        Account account = accountRepository.findById(username).orElseThrow(ElementNotFoundException::new);
        this.accountRepository.updateEnabledByUsername(account.getUsername(), isEnabled);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Modifying
    public void deleteAccountByUsername(String username) throws ElementNotFoundException {
        Account account = accountRepository.findById(username).orElseThrow(ElementNotFoundException::new);
        this.accountDetailsRepository.deleteById(account.getUsername());
        this.accountRepository.deleteById(account.getUsername());
    }

    @Override
    public Boolean matchPasswordFromUsername(String username, String pass) throws ElementNotFoundException {
        Account account = accountRepository.findById(username).orElseThrow(ElementNotFoundException::new);
        return passwordEncoder.matches(pass, account.getPassword());
    }
}
