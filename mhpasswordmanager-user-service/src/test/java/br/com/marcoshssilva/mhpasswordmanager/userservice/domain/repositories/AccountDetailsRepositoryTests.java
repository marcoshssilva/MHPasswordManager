package br.com.marcoshssilva.mhpasswordmanager.userservice.domain.repositories;

import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.entities.AccountDetails;
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
        AccountDetails accountDetails = new AccountDetails();
        accountDetails.setUsername("john_doe");
        accountDetails.setFirstName("John");
        accountDetails.setLastName("Doe");
        accountDetails.setImageUrl("https://example.com/profile.jpg");
        accountDetailsRepository.save(accountDetails);
    }

    @Test
    void testSaveAccountDetails() {
        AccountDetails accountDetails = new AccountDetails();
        accountDetails.setUsername("jane_doe");
        accountDetails.setFirstName("Jane");
        accountDetails.setLastName("Doe");
        accountDetails.setImageUrl("https://example.com/profile.jpg");
        accountDetailsRepository.save(accountDetails);

        assertTrue(accountDetailsRepository.findById(accountDetails.getUsername()).isPresent());
    }

    @Test
    void testUpdateAccountDetails() {
        AccountDetails accountDetails = accountDetailsRepository.findById("john_doe").orElseThrow();
        accountDetails.setImageUrl("https://example.com/new_profile.jpg");
        accountDetailsRepository.save(accountDetails);

        AccountDetails updatedAccountDetails = accountDetailsRepository.findById("john_doe").orElseThrow();
        assertEquals("https://example.com/new_profile.jpg", updatedAccountDetails.getImageUrl());
    }

    @Test
    void testDeleteAccountDetails() {
        AccountDetails accountDetails = accountDetailsRepository.findById("john_doe").orElseThrow();
        accountDetailsRepository.delete(accountDetails);

        assertFalse(accountDetailsRepository.existsById("john_doe"));
    }

    @Test
    void testUpdateAccountDetailsByUsername() {
        AccountDetails accountDetails = accountDetailsRepository.findById("john_doe").orElseThrow();
        String newFirstName = "Maria";
        String newLastName = "Do Carmo";

        accountDetailsRepository.updateAccountDetailsByUsername(accountDetails.getUsername(), "Maria", "Do Carmo");

        Optional<AccountDetails> id = accountDetailsRepository.findById(accountDetails.getUsername());
        if (id.isPresent()) {
            assertEquals(newFirstName, id.get().getFirstName());
            assertEquals(newLastName, id.get().getLastName());
        } else {
            assertEquals(Boolean.TRUE, Boolean.FALSE);
        }
    }
}
