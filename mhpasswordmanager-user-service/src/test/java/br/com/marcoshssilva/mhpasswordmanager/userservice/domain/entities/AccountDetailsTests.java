package br.com.marcoshssilva.mhpasswordmanager.userservice.domain.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AccountDetailsTests {
    @Test
    void testEquals() {
        AccountDetails accountDetails1 = new AccountDetails();
        accountDetails1.setUsername("user1");

        AccountDetails accountDetails2 = new AccountDetails();
        accountDetails2.setUsername("user1");

        assertEquals(accountDetails1, accountDetails2);

        accountDetails2.setUsername("user2");

        assertNotEquals(accountDetails1, accountDetails2);
    }

    @Test
    void testHashCode() {
        AccountDetails accountDetails1 = new AccountDetails();
        accountDetails1.setUsername("user1");

        AccountDetails accountDetails2 = new AccountDetails();
        accountDetails2.setUsername("user1");

        assertEquals(accountDetails1.hashCode(), accountDetails2.hashCode());

        accountDetails2.setUsername("user2");

        assertNotEquals(accountDetails1.hashCode(), accountDetails2.hashCode());
    }

    @Test
    void testGetterAndSetter() {
        AccountDetails accountDetails = new AccountDetails();
        accountDetails.setUsername("user1");
        accountDetails.setFirstName("John");
        accountDetails.setLastName("Doe");
        accountDetails.setImageUrl("https://example.com/avatar.png");

        assertEquals("user1", accountDetails.getUsername());
        assertEquals("John", accountDetails.getFirstName());
        assertEquals("Doe", accountDetails.getLastName());
        assertEquals("https://example.com/avatar.png", accountDetails.getImageUrl());

        accountDetails.setUsername("user2");
        accountDetails.setFirstName("Jane");
        accountDetails.setLastName("Doe");
        accountDetails.setImageUrl(null);

        assertEquals("user2", accountDetails.getUsername());
        assertEquals("Jane", accountDetails.getFirstName());
        assertEquals("Doe", accountDetails.getLastName());
        assertNull(accountDetails.getImageUrl());
    }
}
