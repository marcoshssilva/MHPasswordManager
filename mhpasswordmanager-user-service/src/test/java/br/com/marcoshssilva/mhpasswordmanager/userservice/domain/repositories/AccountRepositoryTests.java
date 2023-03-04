package br.com.marcoshssilva.mhpasswordmanager.userservice.domain.repositories;

import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.entities.Account;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
public class AccountRepositoryTests {
    @Autowired
    private AccountRepository accountRepository;

    @Test
    public void testSave() {
        // create a new Account object
        Account account = new Account();
        account.setUsername("testuser");
        account.setPassword("testpassword");
        account.setEnabled(true);

        // save the Account object
        Account savedAccount = accountRepository.save(account);

        // verify that the saved Account object has the expected values
        assertEquals("testuser", savedAccount.getUsername());
        assertEquals("testpassword", savedAccount.getPassword());
        assertTrue(savedAccount.getEnabled());
    }

    @Test
    @DirtiesContext
    public void testUpdate() {
        // create a new Account object and save it
        Account account = new Account();
        account.setUsername("testuser");
        account.setPassword("testpassword");
        account.setEnabled(true);
        accountRepository.save(account);

        // update the saved Account object
        account.setPassword("newpassword");
        account.setEnabled(false);
        Account updatedAccount = accountRepository.save(account);

        // verify that the updated Account object has the expected values
        assertEquals("testuser", updatedAccount.getUsername());
        assertEquals("newpassword", updatedAccount.getPassword());
        assertFalse(updatedAccount.getEnabled());
    }

    @Test
    @DirtiesContext
    public void testDelete() {
        // create a new Account object and save it
        Account account = new Account();
        account.setUsername("testuser");
        account.setPassword("testpassword");
        account.setEnabled(true);
        accountRepository.save(account);

        // delete the saved Account object
        accountRepository.delete(account);

        // verify that the Account object was deleted
        assertFalse(accountRepository.findById("testuser").isPresent());
    }

    @Test
    public void testGetAccountsByRolesIn() {
        Account account1 = new Account();
        account1.setUsername("johny.bravo@mail.com");
        account1.setPassword("testpassword");
        account1.setEnabled(true);
        account1.getRoles().add("ROLE_ADMIN");

        Account account2 = new Account();
        account2.setUsername("christine.aguero@mail.com");
        account2.setPassword("testpassword");
        account2.setEnabled(true);
        account2.getRoles().add("ROLE_USER");

        accountRepository.save(account1);
        accountRepository.save(account2);

        Set<String> roles = new HashSet<>();
        roles.add("ROLE_ADMIN");
        roles.add("ROLE_USER");
        Page<Account> accounts = accountRepository.getAccountsByRolesIn(roles, PageRequest.of(0, 10));
        assertNotNull(accounts);
        assertEquals(2, accounts.getTotalElements());
    }
}
