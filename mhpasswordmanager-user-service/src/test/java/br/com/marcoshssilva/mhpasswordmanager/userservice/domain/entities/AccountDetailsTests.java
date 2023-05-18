package br.com.marcoshssilva.mhpasswordmanager.userservice.domain.entities;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class AccountDetailsTests {
    @Test
    void testEquals() {
        AccountDetailsPK pk1 = new AccountDetailsPK("mary_lane", "marylane@email.com");
        AccountDetailsPK pk2 = new AccountDetailsPK("john_doe", "johndoe@email.com");
        AccountDetails accountDetails1 = new AccountDetails();
        accountDetails1.setId(pk2);

        AccountDetails accountDetails2 = new AccountDetails();
        accountDetails2.setId(pk2);

        assertEquals(accountDetails1, accountDetails2);

        accountDetails2.setId(pk1);

        assertNotEquals(accountDetails1, accountDetails2);
    }

    @Test
    void testHashCode() {
        AccountDetailsPK pk1 = new AccountDetailsPK("mary_lane", "marylane@email.com");
        AccountDetailsPK pk2 = new AccountDetailsPK("john_doe", "johndoe@email.com");

        AccountDetails accountDetails1 = new AccountDetails();
        accountDetails1.setId(pk1);

        AccountDetails accountDetails2 = new AccountDetails();
        accountDetails2.setId(pk1);

        assertEquals(accountDetails1.hashCode(), accountDetails2.hashCode());

        accountDetails2.setId(pk2);

        assertNotEquals(accountDetails1.hashCode(), accountDetails2.hashCode());
    }

    @Test
    void testGetterAndSetter() {
        Date now = new Date(System.currentTimeMillis());

        AccountDetailsPK pk1 = new AccountDetailsPK("mary_lane", "marylane@email.com");
        AccountDetailsPK pk2 = new AccountDetailsPK("john_doe", "johndoe@email.com");

        AccountDetails accountDetails = new AccountDetails();
        accountDetails.setId(pk1);
        accountDetails.setFirstName("John");
        accountDetails.setLastName("Doe");
        accountDetails.setImageUrl("https://example.com/avatar.png");
        accountDetails.setVerified(true);
        accountDetails.setVerifiedAt(now);

        assertEquals(pk1, accountDetails.getId());
        assertEquals("John", accountDetails.getFirstName());
        assertEquals("Doe", accountDetails.getLastName());
        assertEquals("https://example.com/avatar.png", accountDetails.getImageUrl());

        accountDetails.setId(pk2);
        accountDetails.setFirstName("Jane");
        accountDetails.setLastName("Doe");
        accountDetails.setImageUrl(null);
        accountDetails.setVerifiedAt(null);

        assertEquals(pk2, accountDetails.getId());
        assertEquals("Jane", accountDetails.getFirstName());
        assertEquals("Doe", accountDetails.getLastName());
        assertNull(accountDetails.getImageUrl());
        assertNull(accountDetails.getVerifiedAt());
    }
}
