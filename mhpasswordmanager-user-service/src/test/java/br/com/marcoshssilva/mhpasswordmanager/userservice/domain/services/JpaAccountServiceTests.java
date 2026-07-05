package br.com.marcoshssilva.mhpasswordmanager.userservice.domain.services;

import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.entities.Account;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.entities.AccountDetails;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.entities.AccountDetailsPK;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.models.AccountDataModel;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.repositories.AccountDetailsRepository;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.repositories.AccountRepository;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.repositories.AccountRecoveryPasswordCodeRepository;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.repositories.AccountVerifyCodesRepository;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.services.impl.JpaAccountServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JpaAccountServiceTests {
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AccountDetailsRepository accountDetailsRepository;
    @Mock
    private AccountVerifyCodesRepository accountVerifyCodesRepository;
    @Mock
    private AccountRecoveryPasswordCodeRepository accountRecoveryPasswordCodeRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void shouldLoadUserByUsername() throws Exception {
        Account account = new Account();
        account.setUsername("john");
        account.setPassword("encoded");
        account.setEnabled(true);
        account.setRoles(Set.of("ROLE_USER"));
        AccountDetails accountDetails = AccountDetails.builder()
                .id(new AccountDetailsPK("john", "john@example.com"))
                .firstName("John")
                .lastName("Doe")
                .imageUrl("https://example.com/avatar.png")
                .build();

        when(accountRepository.findById("john")).thenReturn(Optional.of(account));
        when(accountDetailsRepository.getAccountDetailsByIdUsername("john")).thenReturn(Optional.of(accountDetails));

        AccountDataModel result = new JpaAccountServiceImpl(accountRepository, accountDetailsRepository,
                        accountVerifyCodesRepository, accountRecoveryPasswordCodeRepository, passwordEncoder)
                .getUserByUsername("john");

        assertEquals("john", result.username());
        assertEquals("John", result.firstName());
        assertEquals("Doe", result.lastName());
    }
}
