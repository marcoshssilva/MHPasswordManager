package br.com.marcoshssilva.mhpasswordmanager.userservice.domain.services.impl;

import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.entities.Account;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.entities.AccountDetails;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.entities.AccountDetailsPK;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.entities.AccountRecoveryPasswordCode;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.entities.AccountRecoveryPasswordCodePK;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.entities.AccountVerifyCodes;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.enums.DefaultUserRoles;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.exceptions.AlreadyExistsInDatabaseException;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.exceptions.ElementNotFoundException;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.models.AccountDataModel;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.models.AccountDataToUpdateModel;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.models.AccountRegistrationModel;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.models.AccountRecoveryPasswordCodeModel;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.repositories.AccountDetailsRepository;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.repositories.AccountRecoveryPasswordCodeRepository;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.repositories.AccountRepository;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.repositories.AccountVerifyCodesRepository;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.services.AccountService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class JpaAccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final AccountDetailsRepository accountDetailsRepository;
    private final AccountVerifyCodesRepository accountVerifyCodesRepository;
    private final AccountRecoveryPasswordCodeRepository accountRecoveryPasswordCodeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Page<AccountDataModel> getAllUsers(Pageable pageable) {
        return accountRepository.findAll(pageable)
                .map(item -> {
                    Optional<AccountDetails> details = accountDetailsRepository.getAccountDetailsByIdUsername(item.getUsername());
                    return details.map(accountDetails -> new AccountDataModel(item.getUsername(), item.getPassword(), item.getEnabled(), item.getRoles(), accountDetails.getId().getEmail(), accountDetails.getFirstName(), accountDetails.getLastName(), accountDetails.getImageUrl()))
                        .orElseGet(
                            () -> new AccountDataModel(item.getUsername(), item.getPassword(), item.getEnabled(), item.getRoles(), null, null, null, null));
                });
    }

    @Override
    public AccountDataModel getUserByUsername(String username) throws ElementNotFoundException {
        Account account = accountRepository.findById(username).orElseThrow(ElementNotFoundException::new);
        AccountDetails details = accountDetailsRepository.getAccountDetailsByIdUsername(username).orElseThrow(ElementNotFoundException::new);
        return new AccountDataModel(account.getUsername(), account.getPassword(), account.getEnabled(), account.getRoles(), details.getId().getEmail(), details.getFirstName(), details.getLastName(), details.getImageUrl());
    }

    @Override
    public AccountDataModel getUserByEmail(String email) throws ElementNotFoundException {
        AccountDetails details = accountDetailsRepository.getAccountDetailsByIdEmail(email).orElseThrow(ElementNotFoundException::new);
        return getUserByUsername(details.getId().getUsername());
    }

    @Override
    public Boolean existsByUsername(String username) {
        return accountRepository.existsById(username);
    }

    @Override
    public Boolean existsByEmail(String email) {
        return accountDetailsRepository.getAccountDetailsByIdEmail(email).isPresent();
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
        accountDetailsToSave.setId(AccountDetailsPK.builder().email(account.email()).username(account.username()).build());
        accountDetailsToSave.setFirstName(account.firstName());
        accountDetailsToSave.setLastName(account.lastName());
        accountDetailsToSave.setVerified(Boolean.FALSE);

        accountToSave = accountRepository.save(accountToSave);
        accountDetailsToSave = accountDetailsRepository.save(accountDetailsToSave);

        return new AccountDataModel(accountToSave.getUsername(), accountToSave.getPassword(), accountToSave.getEnabled(),
                accountToSave.getRoles(),
                accountDetailsToSave.getId().getEmail(),
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
        this.accountDetailsRepository.deleteAccountDetailsByIdUsername(account.getUsername());
        this.accountRepository.deleteById(account.getUsername());
    }

    @Override
    public Boolean matchPasswordFromUsername(String username, String pass) throws ElementNotFoundException {
        Account account = accountRepository.findById(username).orElseThrow(ElementNotFoundException::new);
        return passwordEncoder.matches(pass, account.getPassword());
    }

    @Override
    public UUID generateAccountVerificationCode(String username) throws ElementNotFoundException {
        accountRepository.findById(username).orElseThrow(ElementNotFoundException::new);
        UUID uuid = UUID.randomUUID();
        accountVerifyCodesRepository.save(AccountVerifyCodes.builder()
                .code(uuid.toString())
                .username(username)
                .createdAt(LocalDateTime.now())
                .build());
        return uuid;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean verifyAccount(String uuidCode) throws ElementNotFoundException {
        AccountVerifyCodes verifyCode = accountVerifyCodesRepository.findById(uuidCode).orElseThrow(ElementNotFoundException::new);
        AccountDetails details = accountDetailsRepository.getAccountDetailsByIdUsername(verifyCode.getUsername()).orElseThrow(ElementNotFoundException::new);
        details.setVerified(Boolean.TRUE);
        details.setVerifiedAt(LocalDateTime.now());
        accountDetailsRepository.save(details);
        accountVerifyCodesRepository.delete(verifyCode);
        return Boolean.TRUE;
    }

    @Override
    public AccountRecoveryPasswordCodeModel saveRecoveryPasswordCode(String username, String code, String ipClient, String userAgentClient) throws ElementNotFoundException {
        accountRepository.findById(username).orElseThrow(ElementNotFoundException::new);
        LocalDateTime now = LocalDateTime.now();
        AccountRecoveryPasswordCode recoveryCode = accountRecoveryPasswordCodeRepository.save(AccountRecoveryPasswordCode.builder()
                .id(AccountRecoveryPasswordCodePK.builder().code(code).username(username).ipClient(ipClient).build())
                .userAgentClient(userAgentClient)
                .createdAt(now)
                .expiresAt(now.plusHours(24))
                .completed(Boolean.FALSE)
                .build());
        return toRecoveryPasswordCodeModel(recoveryCode);
    }

    @Override
    public AccountRecoveryPasswordCodeModel findRecoveryPasswordCode(String code, String ipClient, String userAgentClient) throws ElementNotFoundException {
        return accountRecoveryPasswordCodeRepository.findValidCode(code, ipClient, userAgentClient, LocalDateTime.now())
                .map(this::toRecoveryPasswordCodeModel)
                .orElseThrow(ElementNotFoundException::new);
    }

    private AccountRecoveryPasswordCodeModel toRecoveryPasswordCodeModel(AccountRecoveryPasswordCode recoveryCode) {
        return new AccountRecoveryPasswordCodeModel(
                recoveryCode.getId().getCode(),
                recoveryCode.getId().getUsername(),
                recoveryCode.getId().getIpClient(),
                recoveryCode.getUserAgentClient(),
                recoveryCode.getCreatedAt(),
                recoveryCode.getExpiresAt(),
                recoveryCode.getCompleted());
    }
}
