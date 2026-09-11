package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.impl;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.constants.UserRolesEnum;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.exceptions.UserOperationErrorException;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.RecoveryPasswordCodeRequest;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.RegisteredUserData;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.RequestedBrowserParams;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.web.data.models.UserRegistrationData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InMemoryUserOperationsServiceImplTests {

    @Mock
    private UserDetailsManager userDetailsManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    private InMemoryUserOperationsServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new InMemoryUserOperationsServiceImpl(userDetailsManager, passwordEncoder);
    }

    @DisplayName("Should save user and verify existence by username and email")
    @Test
    void shouldSaveUserAndVerifyExistence() {
        when(passwordEncoder.encode("Password123")).thenReturn("encodedPassword123");
        doNothing().when(userDetailsManager).createUser(any(UserDetails.class));

        UserRegistrationData regData = UserRegistrationData.builder()
                .username("inmem_user")
                .email("inmem@example.com")
                .firstName("InMem")
                .lastName("User")
                .password("Password123")
                .confirmationPassword("Password123")
                .build();

        RegisteredUserData saved = service.saveUser(regData, UserRolesEnum.USER);
        assertNotNull(saved);
        assertEquals("inmem_user", saved.getUsername());
        assertEquals("inmem@example.com", saved.getEmail());

        assertTrue(service.checkIfHasUsernameUsedByAnotherUser("inmem_user"));
        assertTrue(service.checkIfHasEmailUsedByAnotherUser("inmem@example.com"));
        assertFalse(service.checkIfHasUsernameUsedByAnotherUser("other_user"));
        assertFalse(service.checkIfHasEmailUsedByAnotherUser("other@example.com"));
    }

    @DisplayName("Should get user by username and by email")
    @Test
    void shouldGetUserByUsernameAndEmail() {
        when(passwordEncoder.encode("Password123")).thenReturn("encodedPassword123");
        doNothing().when(userDetailsManager).createUser(any(UserDetails.class));

        UserRegistrationData regData = UserRegistrationData.builder()
                .username("lookup_user")
                .email("lookup@example.com")
                .firstName("Look")
                .lastName("Up")
                .password("Password123")
                .build();

        service.saveUser(regData, UserRolesEnum.USER);

        RegisteredUserData byUsername = service.getUserByUsername("lookup_user");
        assertNotNull(byUsername);
        assertEquals("lookup_user", byUsername.getUsername());

        RegisteredUserData byEmail = service.getUserByEmail("lookup@example.com");
        assertNotNull(byEmail);
        assertEquals("lookup_user", byEmail.getUsername());
    }

    @DisplayName("Should throw UserOperationErrorException when getting non-existent user")
    @Test
    void shouldThrowWhenGettingNonExistentUser() {
        assertThrows(UserOperationErrorException.class, () -> service.getUserByUsername("missing_user"));
        assertThrows(UserOperationErrorException.class, () -> service.getUserByEmail("missing@example.com"));
    }

    @DisplayName("Should reset user password")
    @Test
    void shouldResetUserPassword() {
        UserDetails existing = User.withUsername("reset_user").password("old").roles("USER").build();
        when(userDetailsManager.loadUserByUsername("reset_user")).thenReturn(existing);
        when(passwordEncoder.encode("newpass")).thenReturn("encodedNewPass");
        doNothing().when(userDetailsManager).updateUser(any(UserDetails.class));

        service.resetUserPassword("reset_user", "newpass");

        verify(userDetailsManager).updateUser(any(UserDetails.class));
    }

    @DisplayName("Should save and find recovery password code")
    @Test
    void shouldSaveAndFindRecoveryPasswordCode() {
        RegisteredUserData user = RegisteredUserData.builder().username("code_user").build();
        RequestedBrowserParams browserParams = RequestedBrowserParams.builder()
                .ipAddress("192.168.1.1")
                .userAgent("Chrome")
                .build();

        RecoveryPasswordCodeRequest codeReq = service.saveCodeEmailRecoveryPassword(user, "99988877711", browserParams);
        assertNotNull(codeReq);
        assertEquals("99988877711", codeReq.getCode());
        assertEquals("code_user", codeReq.getUsername());

        RecoveryPasswordCodeRequest found = service.findCodeEmailRecoveryPassword("99988877711", browserParams);
        assertNotNull(found);
        assertEquals("99988877711", found.getCode());
    }

    @DisplayName("Should generate UUID verification code and verify account")
    @Test
    void shouldGenerateUUIDCodeAndVerifyAccount() {
        when(passwordEncoder.encode("pass")).thenReturn("enc");
        doNothing().when(userDetailsManager).createUser(any(UserDetails.class));

        UserRegistrationData regData = UserRegistrationData.builder()
                .username("verify_inmem_user")
                .email("verify_inmem@example.com")
                .firstName("V")
                .lastName("U")
                .password("pass")
                .build();

        RegisteredUserData user = service.saveUser(regData, UserRolesEnum.USER);
        UUID uuid = service.generateUUIDCodeToCheckAccountMailVerification(user);
        assertNotNull(uuid);

        RequestedBrowserParams browserParams = RequestedBrowserParams.builder().build();
        Boolean verified = service.verifyUserAccount(uuid.toString(), browserParams);
        assertTrue(verified);

        RegisteredUserData verifiedUser = service.getUserByUsername("verify_inmem_user");
        assertTrue(verifiedUser.getIsEnabled());
    }

    @DisplayName("Should throw exception when verifying non-existent code in memory")
    @Test
    void shouldThrowWhenVerifyingNonExistentCodeInMemory() {
        RequestedBrowserParams browserParams = RequestedBrowserParams.builder().build();
        assertThrows(UserOperationErrorException.class, () -> service.verifyUserAccount("unknown-uuid", browserParams));
    }
}
