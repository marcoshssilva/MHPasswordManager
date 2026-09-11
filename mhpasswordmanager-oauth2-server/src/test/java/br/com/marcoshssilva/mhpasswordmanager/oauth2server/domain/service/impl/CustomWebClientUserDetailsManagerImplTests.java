package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.impl;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.clients.entities.AccountCreateRequestData;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.clients.entities.AccountUserInternalResponseData;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.clients.web.UserServiceWebClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomWebClientUserDetailsManagerImplTests {

    @Mock
    private UserServiceWebClient userServiceWebClient;

    private CustomWebClientUserDetailsManagerImpl userDetailsManager;

    @BeforeEach
    void setUp() {
        userDetailsManager = new CustomWebClientUserDetailsManagerImpl(userServiceWebClient);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @DisplayName("Should create user via client")
    @Test
    void shouldCreateUser() {
        UserDetails user = User.withUsername("testuser").password("password").roles("USER").build();
        doNothing().when(userServiceWebClient).createAccount(any(AccountCreateRequestData.class));

        assertDoesNotThrow(() -> userDetailsManager.createUser(user));
        verify(userServiceWebClient).createAccount(any(AccountCreateRequestData.class));
    }

    @DisplayName("Should update user successfully when user exists")
    @Test
    void shouldUpdateUserSuccessfully() {
        UserDetails user = User.withUsername("testuser").password("newpassword").roles("USER").build();
        AccountUserInternalResponseData account = AccountUserInternalResponseData.builder().username("testuser").build();

        when(userServiceWebClient.getInternalUserFromAccount("testuser")).thenReturn(account);
        doNothing().when(userServiceWebClient).updateAccountEnabled("testuser", true);
        doNothing().when(userServiceWebClient).resetAccountPassword("testuser", "newpassword");

        assertDoesNotThrow(() -> userDetailsManager.updateUser(user));
        verify(userServiceWebClient).updateAccountEnabled("testuser", true);
        verify(userServiceWebClient).resetAccountPassword("testuser", "newpassword");
    }

    @DisplayName("Should throw BadCredentialsException when updating non-existent user")
    @Test
    void shouldThrowWhenUpdatingNonExistentUser() {
        UserDetails user = User.withUsername("missing").password("pass").roles("USER").build();
        when(userServiceWebClient.getInternalUserFromAccount("missing")).thenReturn(null);

        assertThrows(BadCredentialsException.class, () -> userDetailsManager.updateUser(user));
    }

    @DisplayName("Should delete user via client")
    @Test
    void shouldDeleteUser() {
        doNothing().when(userServiceWebClient).deleteAccount("testuser");
        assertDoesNotThrow(() -> userDetailsManager.deleteUser("testuser"));
        verify(userServiceWebClient).deleteAccount("testuser");
    }

    @DisplayName("Should change password successfully when authenticated")
    @Test
    void shouldChangePasswordSuccessfully() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("testuser", "oldpassword", Collections.emptyList())
        );

        when(userServiceWebClient.validatePassword("testuser", "oldpassword")).thenReturn(true);
        doNothing().when(userServiceWebClient).resetAccountPassword("testuser", "newpassword");

        assertDoesNotThrow(() -> userDetailsManager.changePassword("oldpassword", "newpassword"));
        verify(userServiceWebClient).resetAccountPassword("testuser", "newpassword");
    }

    @DisplayName("Should throw BadCredentialsException when not authenticated on changePassword")
    @Test
    void shouldThrowWhenNotAuthenticatedOnChangePassword() {
        assertThrows(BadCredentialsException.class, () -> userDetailsManager.changePassword("old", "new"));
    }

    @DisplayName("Should throw BadCredentialsException when old password is invalid on changePassword")
    @Test
    void shouldThrowWhenOldPasswordInvalidOnChangePassword() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("testuser", "wrongpassword", Collections.emptyList())
        );

        when(userServiceWebClient.validatePassword("testuser", "wrongpassword")).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> userDetailsManager.changePassword("wrongpassword", "new"));
    }

    @DisplayName("Should check userExists")
    @Test
    void shouldCheckUserExists() {
        when(userServiceWebClient.getInternalUserFromAccount("testuser")).thenReturn(AccountUserInternalResponseData.builder().build());
        when(userServiceWebClient.getInternalUserFromAccount("missing")).thenReturn(null);

        assertTrue(userDetailsManager.userExists("testuser"));
        assertFalse(userDetailsManager.userExists("missing"));
    }

    @DisplayName("Should load user by username successfully")
    @Test
    void shouldLoadUserByUsername() {
        AccountUserInternalResponseData account = AccountUserInternalResponseData.builder()
                .username("testuser")
                .password("encodedPass")
                .enabled(true)
                .roles(Set.of("ROLE_USER"))
                .build();

        when(userServiceWebClient.getInternalUserFromAccount("testuser")).thenReturn(account);

        UserDetails user = userDetailsManager.loadUserByUsername("testuser");
        assertNotNull(user);
        assertEquals("testuser", user.getUsername());
        assertEquals("encodedPass", user.getPassword());
        assertTrue(user.isEnabled());
    }

    @DisplayName("Should throw UsernameNotFoundException when user not found")
    @Test
    void shouldThrowWhenUserNotFoundOnLoad() {
        when(userServiceWebClient.getInternalUserFromAccount("missing")).thenReturn(null);
        assertThrows(UsernameNotFoundException.class, () -> userDetailsManager.loadUserByUsername("missing"));
    }
}
