package br.com.marcoshssilva.mhpasswordmanager.userservice.domain.repositories;

import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.entities.Account;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.entities.AccountDetails;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.entities.AccountDetailsPK;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
@Transactional
class AccountDetailsRepositoryTests {
    @Autowired
    private AccountDetailsRepository accountDetailsRepository;

    @BeforeEach
    void setUp() {
        AccountDetailsPK pk = new AccountDetailsPK("john_doe", "johndoe@email.com");

        AccountDetails accountDetails = new AccountDetails();
        accountDetails.setId(pk);
        accountDetails.setFirstName("John");
        accountDetails.setLastName("Doe");
        accountDetails.setImageUrl("https://example.com/profile.jpg");
        accountDetailsRepository.save(accountDetails);
    }

    @Test
    void testSaveAccountDetails() {
        AccountDetails accountDetails = new AccountDetails();
        AccountDetailsPK pk = new AccountDetailsPK("john_doe", "johndoe@email.com");
        accountDetails.setId(pk);
        accountDetails.setFirstName("Jane");
        accountDetails.setLastName("Doe");
        accountDetails.setImageUrl("https://example.com/profile.jpg");
        accountDetailsRepository.save(accountDetails);

        assertTrue(accountDetailsRepository.findById(pk).isPresent());
    }

    @Test
    void testUpdateAccountDetails() {
        AccountDetailsPK pk = new AccountDetailsPK("john_doe", "johndoe@email.com");
        AccountDetails accountDetails = accountDetailsRepository.findById(pk).orElseThrow();
        accountDetails.setImageUrl("https://example.com/new_profile.jpg");
        accountDetailsRepository.save(accountDetails);

        AccountDetails updatedAccountDetails = accountDetailsRepository.findById(pk).orElseThrow();
        assertEquals("https://example.com/new_profile.jpg", updatedAccountDetails.getImageUrl());
    }

    @Test
    void testDeleteAccountDetails() {
        AccountDetailsPK pk = new AccountDetailsPK("john_doe", "johndoe@email.com");
        AccountDetails accountDetails = accountDetailsRepository.findById(pk).orElseThrow();
        accountDetailsRepository.delete(accountDetails);

        assertFalse(accountDetailsRepository.existsById(pk));
    }

    @Test
    void testUpdateAccountDetailsByUsername() {
        AccountDetailsPK pk = new AccountDetailsPK("john_doe", "johndoe@email.com");
        AccountDetails accountDetails = accountDetailsRepository.findById(pk).orElseThrow();
        String newFirstName = "Maria";
        String newLastName = "Do Carmo";

        accountDetailsRepository.updateAccountDetailsByUsername(accountDetails.getId().getUsername(), "Maria", "Do Carmo");

        Optional<AccountDetails> id = accountDetailsRepository.findById(pk);
        if (id.isPresent()) {
            assertEquals(newFirstName, id.get().getFirstName());
            assertEquals(newLastName, id.get().getLastName());
        } else {
            assertEquals(Boolean.TRUE, Boolean.FALSE);
        }
    }
}
