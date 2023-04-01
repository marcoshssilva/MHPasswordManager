package br.com.marcoshssilva.mhpasswordmanager.userservice.domain.services;

import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.entities.Account;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.entities.AccountDetails;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.repositories.AccountDetailsRepository;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.repositories.AccountRepository;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.services.impl.JpaAccountServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class JpaAccountServiceTests {
    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountDetailsRepository accountDetailsRepository;

    @InjectMocks
    private JpaAccountServiceImpl accountService;

    @Test
    public void testGetAllUsers() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Account> accounts = new PageImpl<>(Collections.emptyList(), pageable, 0L);
        Mockito.when(accountRepository.findAll(pageable)).thenReturn(accounts);

        // when
        Page<Account> result = accountService.getAllUsers(pageable);

        // then
        assertEquals(accounts, result);
        Mockito.verify(accountRepository).findAll(pageable);
    }

    @Test
    public void testGetAllUsersByRole() {
        // given
        String role = "ADMIN";
        Set<String> stringSet = Collections.singleton("ROLE_ADMIN");
        Pageable pageable = PageRequest.of(0, 10);
        Page<Account> accounts = new PageImpl<>(Collections.emptyList(), pageable, 0L);
        Mockito.when(accountRepository.getAccountsByRolesIn(stringSet, pageable)).thenReturn(accounts);

        // when
        Page<Account> result = accountService.getAllUsersByRole(role, pageable);

        // then
        assertEquals(accounts, result);
        Mockito.verify(accountRepository).getAccountsByRolesIn(stringSet, pageable);
    }

    @Test
    public void testGetUserByUsername() {
        // given
        String username = "testuser";
        Account account = new Account();
        account.setUsername(username);
        Mockito.when(accountRepository.findById(username)).thenReturn(Optional.of(account));

        // when
        Account result = accountService.getUserByUsername(username);

        // then
        assertEquals(account, result);
        Mockito.verify(accountRepository).findById(username);
    }

    @Test
    public void testSaveDetails() {
        // given
        String username = "testuser";

        Optional<Account> account = Optional.of(new Account(username, "234849", Boolean.TRUE, Set.of("ROLE_ADMIN")));
        AccountDetails accountDetails = new AccountDetails();
        accountDetails.setUsername(username);

        // when
        Mockito.when(accountRepository.findById(username)).thenReturn(account);
        accountService.saveDetails(accountDetails);

        // then
        Mockito.verify(accountDetailsRepository).save(accountDetails);
    }
}
