package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.impl;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.constants.UserRolesEnum;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.exceptions.UserOperationErrorException;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.RecoveryPasswordCodeRequest;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.RegisteredUserData;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.RequestedBrowserParams;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.UserOperationsService;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.web.data.models.UserRegistrationData;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class InMemoryUserOperationsServiceImpl implements UserOperationsService {

    private static final List<RegisteredUserData> USERS = new ArrayList<>();
    private static final List<RecoveryPasswordCodeRequest> RECOVERY_CODES = new ArrayList<>();
    private static final List<String[]> VERIFY_CODES = new ArrayList<>();
    private static final ReadWriteLock LOCK = new ReentrantReadWriteLock();

    private final UserDetailsManager userDetailsManager;
    private final PasswordEncoder passwordEncoder;

    public InMemoryUserOperationsServiceImpl(UserDetailsManager userDetailsManager,
            PasswordEncoder passwordEncoder) {
        this.userDetailsManager = userDetailsManager;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Boolean checkIfHasEmailUsedByAnotherUser(String email) {
        LOCK.readLock().lock();
        try {
            return USERS.stream()
                    .anyMatch(u -> email != null && email.equalsIgnoreCase(u.getEmail()));
        } finally {
            LOCK.readLock().unlock();
        }
    }

    @Override
    public Boolean checkIfHasUsernameUsedByAnotherUser(String username) {
        LOCK.readLock().lock();
        try {
            return USERS.stream()
                    .anyMatch(u -> username != null && username.equalsIgnoreCase(u.getUsername()));
        } finally {
            LOCK.readLock().unlock();
        }
    }

    @Override
    public UUID generateUUIDCodeToCheckAccountMailVerification(RegisteredUserData client) {
        UUID uuid = UUID.randomUUID();
        LOCK.writeLock().lock();
        try {
            VERIFY_CODES.add(new String[] { uuid.toString(), client.getUsername() });
        } finally {
            LOCK.writeLock().unlock();
        }
        return uuid;
    }

    @Override
    public void resetUserPassword(String username, String newPassword) {
        String encodedPassword = passwordEncoder.encode(newPassword);
        UserDetails existing = userDetailsManager
                .loadUserByUsername(username);
        userDetailsManager.updateUser(
                User.withUserDetails(existing)
                        .password(encodedPassword)
                        .build());
    }

    @Override
    public RecoveryPasswordCodeRequest saveCodeEmailRecoveryPassword(RegisteredUserData client,
            String code,
            RequestedBrowserParams requestedBrowserParams) {
        RecoveryPasswordCodeRequest request = RecoveryPasswordCodeRequest.builder()
                .code(code)
                .username(client.getUsername())
                .ipClient(requestedBrowserParams.getIpAddress())
                .userAgentClient(requestedBrowserParams.getUserAgent())
                .createdAt(LocalDate.now(ZoneId.systemDefault()))
                .expiresAt(LocalDate.now(ZoneId.systemDefault()).plusDays(1))
                .completed(Boolean.FALSE)
                .build();

        LOCK.writeLock().lock();
        try {
            RECOVERY_CODES.add(request);
        } finally {
            LOCK.writeLock().unlock();
        }

        return findCodeEmailRecoveryPassword(code, requestedBrowserParams);
    }

    @Override
    public RecoveryPasswordCodeRequest findCodeEmailRecoveryPassword(String code,
            RequestedBrowserParams requestedBrowserParams) {
        LOCK.readLock().lock();
        try {
            return RECOVERY_CODES.stream()
                    .filter(r -> r.getCode().equals(code))
                    .filter(r -> r.getIpClient().equals(requestedBrowserParams.getIpAddress()))
                    .filter(r -> r.getUserAgentClient().equals(requestedBrowserParams.getUserAgent()))
                    .filter(r -> Boolean.FALSE.equals(r.getCompleted()))
                    .filter(r -> r.getExpiresAt() != null && !r.getExpiresAt().isBefore(LocalDate.now(ZoneId.systemDefault())))
                    .findFirst()
                    .orElseThrow(() -> new UserOperationErrorException("Recovery code not found"));
        } finally {
            LOCK.readLock().unlock();
        }
    }

    @Override
    public RegisteredUserData saveUser(UserRegistrationData userRegistrationData, UserRolesEnum role) {
        // 1. Register the user in the Spring Security in-memory store
        userDetailsManager.createUser(
                User.withUsername(userRegistrationData.getUsername())
                        .password(passwordEncoder.encode(userRegistrationData.getPassword()))
                        .roles(role.name())
                        .build());

        // 2. Persist user details in the in-memory store (unverified by default, same
        // as JDBC)
        RegisteredUserData userData = RegisteredUserData.builder()
                .username(userRegistrationData.getUsername())
                .email(userRegistrationData.getEmail())
                .firstName(userRegistrationData.getFirstName())
                .lastName(userRegistrationData.getLastName())
                .isEnabled(Boolean.FALSE)
                .build();

        LOCK.writeLock().lock();
        try {
            USERS.add(userData);
        } finally {
            LOCK.writeLock().unlock();
        }

        return getUserByUsername(userRegistrationData.getUsername());
    }

    @Override
    public RegisteredUserData getUserByUsername(String username) {
        LOCK.readLock().lock();
        try {
            return USERS.stream()
                    .filter(u -> u.getUsername().equals(username))
                    .findFirst()
                    .orElseThrow(() -> new UserOperationErrorException("User not found with username: " + username));
        } finally {
            LOCK.readLock().unlock();
        }
    }

    @Override
    public RegisteredUserData getUserByEmail(String email) {
        LOCK.readLock().lock();
        try {
            return USERS.stream()
                    .filter(u -> email != null && email.equalsIgnoreCase(u.getEmail()))
                    .findFirst()
                    .orElseThrow(() -> new UserOperationErrorException("User not found with email: " + email));
        } finally {
            LOCK.readLock().unlock();
        }
    }

    @Override
    public Boolean verifyUserAccount(String uuidCode, RequestedBrowserParams browserParams) {
        LOCK.writeLock().lock();
        try {
            Optional<String[]> verifyEntry = VERIFY_CODES.stream()
                    .filter(entry -> entry[0].equals(uuidCode))
                    .findFirst();

            if (verifyEntry.isEmpty()) {
                throw new UserOperationErrorException("User not found for verification code: " + uuidCode);
            }

            String username = verifyEntry.get()[1];

            // Mark the user as enabled (verified)
            Optional<RegisteredUserData> data = USERS.stream().filter(u -> u.getUsername().equals(username)).findFirst();
            data.ifPresent(u -> u.setIsEnabled(Boolean.TRUE));
            boolean userUpdated = data.isPresent();

            // Remove the consumed verification code
            boolean codeRemoved = VERIFY_CODES.removeIf(entry -> entry[0].equals(uuidCode));

            return userUpdated && codeRemoved;
        } finally {
            LOCK.writeLock().unlock();
        }
    }
}
