package br.com.marcoshssilva.mhpasswordmanager.userservice.domain.services;

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
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.models.AccountRecoveryPasswordCodeModel;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.models.AccountRegistrationModel;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.repositories.AccountDetailsRepository;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.repositories.AccountRecoveryPasswordCodeRepository;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.repositories.AccountRepository;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.repositories.AccountVerifyCodesRepository;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.services.impl.JpaAccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
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

    private JpaAccountServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new JpaAccountServiceImpl(
                accountRepository,
                accountDetailsRepository,
                accountVerifyCodesRepository,
                accountRecoveryPasswordCodeRepository,
                passwordEncoder
        );
    }

    @DisplayName("Should return all users paged with account details when present")
    @Test
    void shouldGetAllUsersWithDetails() {
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

        Pageable pageable = PageRequest.of(0, 10);
        when(accountRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(account)));
        when(accountDetailsRepository.getAccountDetailsByIdUsername("john")).thenReturn(Optional.of(accountDetails));

        Page<AccountDataModel> result = service.getAllUsers(pageable);

        assertEquals(1, result.getTotalElements());
        AccountDataModel data = result.getContent().get(0);
        assertEquals("john", data.username());
        assertEquals("john@example.com", data.email());
        assertEquals("John", data.firstName());
        assertEquals("Doe", data.lastName());
    }

    @DisplayName("Should return all users paged with null details when details absent")
    @Test
    void shouldGetAllUsersWithoutDetails() {
        Account account = new Account();
        account.setUsername("john");
        account.setPassword("encoded");
        account.setEnabled(true);
        account.setRoles(Set.of("ROLE_USER"));

        Pageable pageable = PageRequest.of(0, 10);
        when(accountRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(account)));
        when(accountDetailsRepository.getAccountDetailsByIdUsername("john")).thenReturn(Optional.empty());

        Page<AccountDataModel> result = service.getAllUsers(pageable);

        assertEquals(1, result.getTotalElements());
        AccountDataModel data = result.getContent().get(0);
        assertEquals("john", data.username());
        assertNull(data.email());
        assertNull(data.firstName());
        assertNull(data.lastName());
    }

    @DisplayName("Should load user by username successfully")
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

        AccountDataModel result = service.getUserByUsername("john");

        assertEquals("john", result.username());
        assertEquals("John", result.firstName());
        assertEquals("Doe", result.lastName());
    }

    @DisplayName("Should throw ElementNotFoundException when getUserByUsername finds no account")
    @Test
    void shouldThrowWhenGetUserByUsernameAccountNotFound() {
        when(accountRepository.findById("unknown")).thenReturn(Optional.empty());

        assertThrows(ElementNotFoundException.class, () -> service.getUserByUsername("unknown"));
    }

    @DisplayName("Should throw ElementNotFoundException when getUserByUsername finds no details")
    @Test
    void shouldThrowWhenGetUserByUsernameDetailsNotFound() {
        Account account = new Account();
        account.setUsername("john");
        when(accountRepository.findById("john")).thenReturn(Optional.of(account));
        when(accountDetailsRepository.getAccountDetailsByIdUsername("john")).thenReturn(Optional.empty());

        assertThrows(ElementNotFoundException.class, () -> service.getUserByUsername("john"));
    }

    @DisplayName("Should load user by email successfully")
    @Test
    void shouldGetUserByEmail() throws Exception {
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

        when(accountDetailsRepository.getAccountDetailsByIdEmail("john@example.com")).thenReturn(Optional.of(accountDetails));
        when(accountRepository.findById("john")).thenReturn(Optional.of(account));
        when(accountDetailsRepository.getAccountDetailsByIdUsername("john")).thenReturn(Optional.of(accountDetails));

        AccountDataModel result = service.getUserByEmail("john@example.com");

        assertEquals("john", result.username());
        assertEquals("john@example.com", result.email());
    }

    @DisplayName("Should throw ElementNotFoundException when getUserByEmail finds no details")
    @Test
    void shouldThrowWhenGetUserByEmailNotFound() {
        when(accountDetailsRepository.getAccountDetailsByIdEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThrows(ElementNotFoundException.class, () -> service.getUserByEmail("unknown@example.com"));
    }

    @DisplayName("Should check exists by username")
    @Test
    void shouldCheckExistsByUsername() {
        when(accountRepository.existsById("john")).thenReturn(true);
        when(accountRepository.existsById("unknown")).thenReturn(false);

        assertTrue(service.existsByUsername("john"));
        assertFalse(service.existsByUsername("unknown"));
    }

    @DisplayName("Should check exists by email")
    @Test
    void shouldCheckExistsByEmail() {
        AccountDetails accountDetails = AccountDetails.builder()
                .id(new AccountDetailsPK("john", "john@example.com"))
                .build();

        when(accountDetailsRepository.getAccountDetailsByIdEmail("john@example.com")).thenReturn(Optional.of(accountDetails));
        when(accountDetailsRepository.getAccountDetailsByIdEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertTrue(service.existsByEmail("john@example.com"));
        assertFalse(service.existsByEmail("unknown@example.com"));
    }

    @DisplayName("Should register account successfully")
    @Test
    void shouldRegisterAccount() throws Exception {
        AccountRegistrationModel regModel = new AccountRegistrationModel(
                "john@example.com",
                "john",
                "rawPassword",
                Boolean.TRUE,
                Set.of(DefaultUserRoles.USER),
                "John",
                "Doe"
        );

        when(accountRepository.findById("john")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");

        Account savedAccount = new Account();
        savedAccount.setUsername("john");
        savedAccount.setPassword("encodedPassword");
        savedAccount.setEnabled(Boolean.TRUE);
        savedAccount.setRoles(Set.of(DefaultUserRoles.USER.getValue()));

        AccountDetails savedDetails = new AccountDetails();
        savedDetails.setId(AccountDetailsPK.builder().email("john@example.com").username("john").build());
        savedDetails.setFirstName("John");
        savedDetails.setLastName("Doe");
        savedDetails.setVerified(Boolean.FALSE);

        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);
        when(accountDetailsRepository.save(any(AccountDetails.class))).thenReturn(savedDetails);

        AccountDataModel result = service.register(regModel);

        assertNotNull(result);
        assertEquals("john", result.username());
        assertEquals("encodedPassword", result.password());
        assertEquals("john@example.com", result.email());
        assertEquals("John", result.firstName());
        assertEquals("Doe", result.lastName());
    }

    @DisplayName("Should throw AlreadyExistsInDatabaseException when username already registered")
    @Test
    void shouldThrowAlreadyExistsWhenRegisteringExistingUser() {
        AccountRegistrationModel regModel = new AccountRegistrationModel(
                "john@example.com",
                "john",
                "rawPassword",
                Boolean.TRUE,
                Set.of(DefaultUserRoles.USER),
                "John",
                "Doe"
        );

        when(accountRepository.findById("john")).thenReturn(Optional.of(new Account()));

        assertThrows(AlreadyExistsInDatabaseException.class, () -> service.register(regModel));
    }

    @DisplayName("Should update account details by username")
    @Test
    void shouldUpdateAccountDetailsByUsername() throws Exception {
        Account account = new Account();
        account.setUsername("john");
        when(accountRepository.findById("john")).thenReturn(Optional.of(account));

        service.updateAccountDetailsByUsername("john", new AccountDataToUpdateModel("Johnny", "Walker"));

        verify(accountDetailsRepository).updateAccountDetailsByUsername("john", "Johnny", "Walker");
    }

    @DisplayName("Should throw ElementNotFoundException on updateAccountDetailsByUsername when not found")
    @Test
    void shouldThrowOnUpdateAccountDetailsWhenNotFound() {
        when(accountRepository.findById("unknown")).thenReturn(Optional.empty());

        assertThrows(ElementNotFoundException.class,
                () -> service.updateAccountDetailsByUsername("unknown", new AccountDataToUpdateModel("Johnny", "Walker")));
    }

    @DisplayName("Should update password by username")
    @Test
    void shouldUpdatePasswordByUsername() throws Exception {
        Account account = new Account();
        account.setUsername("john");
        when(accountRepository.findById("john")).thenReturn(Optional.of(account));
        when(passwordEncoder.encode("newPass")).thenReturn("encodedNewPass");

        service.updatePasswordByUsername("john", "newPass");

        verify(accountRepository).updatePasswordByUsername("john", "encodedNewPass");
    }

    @DisplayName("Should throw ElementNotFoundException on updatePasswordByUsername when not found")
    @Test
    void shouldThrowOnUpdatePasswordWhenNotFound() {
        when(accountRepository.findById("unknown")).thenReturn(Optional.empty());

        assertThrows(ElementNotFoundException.class, () -> service.updatePasswordByUsername("unknown", "newPass"));
    }

    @DisplayName("Should update account enabled by username")
    @Test
    void shouldUpdateAccountEnabledByUsername() throws Exception {
        Account account = new Account();
        account.setUsername("john");
        when(accountRepository.findById("john")).thenReturn(Optional.of(account));

        service.updateAccountHasEnabledByUsername("john", Boolean.FALSE);

        verify(accountRepository).updateEnabledByUsername("john", Boolean.FALSE);
    }

    @DisplayName("Should throw ElementNotFoundException on updateAccountHasEnabledByUsername when not found")
    @Test
    void shouldThrowOnUpdateEnabledWhenNotFound() {
        when(accountRepository.findById("unknown")).thenReturn(Optional.empty());

        assertThrows(ElementNotFoundException.class, () -> service.updateAccountHasEnabledByUsername("unknown", Boolean.TRUE));
    }

    @DisplayName("Should delete account by username")
    @Test
    void shouldDeleteAccountByUsername() throws Exception {
        Account account = new Account();
        account.setUsername("john");
        when(accountRepository.findById("john")).thenReturn(Optional.of(account));

        service.deleteAccountByUsername("john");

        verify(accountDetailsRepository).deleteAccountDetailsByIdUsername("john");
        verify(accountRepository).deleteById("john");
    }

    @DisplayName("Should throw ElementNotFoundException on deleteAccountByUsername when not found")
    @Test
    void shouldThrowOnDeleteAccountWhenNotFound() {
        when(accountRepository.findById("unknown")).thenReturn(Optional.empty());

        assertThrows(ElementNotFoundException.class, () -> service.deleteAccountByUsername("unknown"));
    }

    @DisplayName("Should match password from username")
    @Test
    void shouldMatchPasswordFromUsername() throws Exception {
        Account account = new Account();
        account.setUsername("john");
        account.setPassword("encodedPass");
        when(accountRepository.findById("john")).thenReturn(Optional.of(account));
        when(passwordEncoder.matches("rawPass", "encodedPass")).thenReturn(true);
        when(passwordEncoder.matches("wrongPass", "encodedPass")).thenReturn(false);

        assertTrue(service.matchPasswordFromUsername("john", "rawPass"));
        assertFalse(service.matchPasswordFromUsername("john", "wrongPass"));
    }

    @DisplayName("Should throw ElementNotFoundException on matchPasswordFromUsername when not found")
    @Test
    void shouldThrowOnMatchPasswordWhenNotFound() {
        when(accountRepository.findById("unknown")).thenReturn(Optional.empty());

        assertThrows(ElementNotFoundException.class, () -> service.matchPasswordFromUsername("unknown", "pass"));
    }

    @DisplayName("Should generate account verification code")
    @Test
    void shouldGenerateAccountVerificationCode() throws Exception {
        Account account = new Account();
        account.setUsername("john");
        when(accountRepository.findById("john")).thenReturn(Optional.of(account));

        UUID code = service.generateAccountVerificationCode("john");

        assertNotNull(code);
        ArgumentCaptor<AccountVerifyCodes> captor = ArgumentCaptor.forClass(AccountVerifyCodes.class);
        verify(accountVerifyCodesRepository).save(captor.capture());
        assertEquals("john", captor.getValue().getUsername());
        assertEquals(code.toString(), captor.getValue().getCode());
    }

    @DisplayName("Should throw ElementNotFoundException on generateAccountVerificationCode when user not found")
    @Test
    void shouldThrowOnGenerateVerificationCodeWhenUserNotFound() {
        when(accountRepository.findById("unknown")).thenReturn(Optional.empty());

        assertThrows(ElementNotFoundException.class, () -> service.generateAccountVerificationCode("unknown"));
    }

    @DisplayName("Should verify account successfully")
    @Test
    void shouldVerifyAccount() throws Exception {
        String code = UUID.randomUUID().toString();
        AccountVerifyCodes verifyCode = AccountVerifyCodes.builder()
                .code(code)
                .username("john")
                .createdAt(LocalDateTime.now())
                .build();

        AccountDetails details = AccountDetails.builder()
                .id(new AccountDetailsPK("john", "john@example.com"))
                .verified(Boolean.FALSE)
                .build();

        when(accountVerifyCodesRepository.findById(code)).thenReturn(Optional.of(verifyCode));
        when(accountDetailsRepository.getAccountDetailsByIdUsername("john")).thenReturn(Optional.of(details));

        Boolean result = service.verifyAccount(code);

        assertTrue(result);
        assertTrue(details.getVerified());
        assertNotNull(details.getVerifiedAt());
        verify(accountDetailsRepository).save(details);
        verify(accountVerifyCodesRepository).delete(verifyCode);
    }

    @DisplayName("Should throw ElementNotFoundException on verifyAccount when code not found")
    @Test
    void shouldThrowOnVerifyAccountWhenCodeNotFound() {
        when(accountVerifyCodesRepository.findById("code123")).thenReturn(Optional.empty());

        assertThrows(ElementNotFoundException.class, () -> service.verifyAccount("code123"));
    }

    @DisplayName("Should throw ElementNotFoundException on verifyAccount when details not found")
    @Test
    void shouldThrowOnVerifyAccountWhenDetailsNotFound() {
        String code = "code123";
        AccountVerifyCodes verifyCode = AccountVerifyCodes.builder().code(code).username("john").build();
        when(accountVerifyCodesRepository.findById(code)).thenReturn(Optional.of(verifyCode));
        when(accountDetailsRepository.getAccountDetailsByIdUsername("john")).thenReturn(Optional.empty());

        assertThrows(ElementNotFoundException.class, () -> service.verifyAccount(code));
    }

    @DisplayName("Should save recovery password code")
    @Test
    void shouldSaveRecoveryPasswordCode() throws Exception {
        Account account = new Account();
        account.setUsername("john");
        when(accountRepository.findById("john")).thenReturn(Optional.of(account));

        AccountRecoveryPasswordCode recoveryCode = AccountRecoveryPasswordCode.builder()
                .id(AccountRecoveryPasswordCodePK.builder().code("123456").username("john").ipClient("127.0.0.1").build())
                .userAgentClient("Postman")
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusHours(24))
                .completed(Boolean.FALSE)
                .build();

        when(accountRecoveryPasswordCodeRepository.save(any(AccountRecoveryPasswordCode.class))).thenReturn(recoveryCode);

        AccountRecoveryPasswordCodeModel model = service.saveRecoveryPasswordCode("john", "123456", "127.0.0.1", "Postman");

        assertNotNull(model);
        assertEquals("123456", model.code());
        assertEquals("john", model.username());
        assertEquals("127.0.0.1", model.ipClient());
        assertEquals("Postman", model.userAgentClient());
        assertEquals(Boolean.FALSE, model.completed());
    }

    @DisplayName("Should throw ElementNotFoundException on saveRecoveryPasswordCode when user not found")
    @Test
    void shouldThrowOnSaveRecoveryPasswordCodeWhenUserNotFound() {
        when(accountRepository.findById("unknown")).thenReturn(Optional.empty());

        assertThrows(ElementNotFoundException.class, () -> service.saveRecoveryPasswordCode("unknown", "123", "ip", "agent"));
    }

    @DisplayName("Should find recovery password code")
    @Test
    void shouldFindRecoveryPasswordCode() throws Exception {
        AccountRecoveryPasswordCode recoveryCode = AccountRecoveryPasswordCode.builder()
                .id(AccountRecoveryPasswordCodePK.builder().code("123456").username("john").ipClient("127.0.0.1").build())
                .userAgentClient("Postman")
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusHours(24))
                .completed(Boolean.FALSE)
                .build();

        when(accountRecoveryPasswordCodeRepository.findValidCode(eq("123456"), eq("127.0.0.1"), eq("Postman"), any(LocalDateTime.class)))
                .thenReturn(Optional.of(recoveryCode));

        AccountRecoveryPasswordCodeModel model = service.findRecoveryPasswordCode("123456", "127.0.0.1", "Postman");

        assertNotNull(model);
        assertEquals("123456", model.code());
        assertEquals("john", model.username());
    }

    @DisplayName("Should throw ElementNotFoundException on findRecoveryPasswordCode when not found")
    @Test
    void shouldThrowOnFindRecoveryPasswordCodeWhenNotFound() {
        when(accountRecoveryPasswordCodeRepository.findValidCode(eq("123456"), eq("127.0.0.1"), eq("Postman"), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        assertThrows(ElementNotFoundException.class, () -> service.findRecoveryPasswordCode("123456", "127.0.0.1", "Postman"));
    }
}
