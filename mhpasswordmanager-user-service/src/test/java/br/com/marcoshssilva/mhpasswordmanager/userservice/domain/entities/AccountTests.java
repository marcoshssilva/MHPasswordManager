package br.com.marcoshssilva.mhpasswordmanager.userservice.domain.entities;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class AccountTests {
    @Test
    void testEqualsAndHashCode() {
        Account account1 = new Account("user1", "password1", true, Set.of("ROLE_USER"));
        Account account2 = new Account("user1", "password2", false, Set.of("ROLE_ADMIN"));
        Account account3 = new Account("user2", "password3", true, Set.of("ROLE_USER"));

        // Test equals method
        assertEquals(account1, account2);
        assertNotEquals(account1, account3);
        assertNotEquals(account2, account3);

        // Test hashCode method
        Set<Account> accountSet = new HashSet<>();
        accountSet.add(account1);
        accountSet.add(account2);
        accountSet.add(account3);

        assertEquals(2, accountSet.size());
    }

    @Test
    void testGetterAndSetter() {
        Account account = new Account();
        account.setUsername("user1");
        account.setPassword("password1");
        account.setEnabled(true);
        account.getRoles().add("ROLE_USER");

        assertEquals("user1", account.getUsername());
        assertEquals("password1", account.getPassword());
        assertTrue(account.getEnabled());
        assertTrue(account.getRoles().contains("ROLE_USER"));

        account.setUsername("user2");
        account.setPassword("password2");
        account.setEnabled(false);
        account.getRoles().clear();
        account.getRoles().add("ROLE_ADMIN");

        assertEquals("user2", account.getUsername());
        assertEquals("password2", account.getPassword());
        assertFalse(account.getEnabled());
        assertTrue(account.getRoles().contains("ROLE_ADMIN"));
    }
}
