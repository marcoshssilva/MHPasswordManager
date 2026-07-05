package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.impl;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.constants.UserRolesEnum;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.RecoveryPasswordCodeRequest;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.RegisteredUserData;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.RequestedBrowserParams;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.UserOperationsService;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.mappers.RecoveryPasswordCodeRequestMapper;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.mappers.RegisteredUserDataMapper;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.web.data.models.UserRegistrationData;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class JdbcUserOperationsServiceImpl implements UserOperationsService {
    public static final String QUERY_SEARCH_IF_USERNAME_EXISTS = "SELECT count(1) FROM users u  WHERE u.username = ?";
    public static final String QUERY_SEARCH_IF_EMAIL_EXISTS = "SELECT count(1) FROM users u INNER JOIN users_details ud ON u.username = ud.username WHERE ud.email = ?";
    public static final String QUERY_GET_BY_USERNAME = "SELECT ud.username, ud.email, ud.firstname, ud.lastName, u.enabled FROM users u INNER JOIN users_details ud ON u.username = ud.username WHERE ud.username = ?";
    public static final String QUERY_GET_BY_EMAIL = "SELECT ud.username, ud.email, ud.firstname, ud.lastName, u.enabled FROM users u INNER JOIN users_details ud ON u.username = ud.username WHERE ud.email = ?";
    public static final String QUERY_UPDATE_USER_PASSWORD = "UPDATE users u SET password = ? WHERE username = ?";
    public static final String QUERY_SAVE_RECOVERY_CODE_GENERATED_FOR_USER = "INSERT INTO users_recovery_password_code(code, username, ip_client, user_agent_client, created_at, expires_at, completed) VALUES(?, ?, ?, ?, ?, ?, ?)";
    public static final String QUERY_GET_RECOVERY_CODE_GENERATED_FOR_USER = "SELECT code, username, ip_client, user_agent_client, created_at, expires_at, completed FROM users_recovery_password_code WHERE code = ? AND ip_client = ? AND user_agent_client = ? AND completed = false AND expires_at > ?";
    public static final String QUERY_GET_USERNAME_BY_UUID_CODE = "SELECT username FROM users_verify_codes WHERE uuid_code = ?";
    public static final String QUERY_UPDATE_USER_DETAILS_VERIFIED = "UPDATE users_details SET verified = true, verified_at = current_timestamp WHERE username = ?";
    public static final String QUERY_DELETE_USER_VERIFY_CODE = "DELETE FROM users_verify_codes WHERE uuid_code = ?";
    public static final String QUERY_SAVE_USER_DETAILS = "INSERT INTO users_details(username, email, firstname, lastname, verified) values(?, ?, ?, ?, ?)";
    public static final String QUERY_SAVE_VERIFY_CODE = "INSERT INTO users_verify_codes(uuid_code, username) VALUES (?, ?)";

    private final JdbcTemplate jdbcTemplate;
    private final UserDetailsManager userDetailsManager;
    private final PasswordEncoder passwordEncoder;

    public JdbcUserOperationsServiceImpl(PasswordEncoder passwordEncoder, JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.userDetailsManager = new JdbcUserDetailsManager(jdbcTemplate.getDataSource());
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Boolean checkIfHasEmailUsedByAnotherUser(String email) {
        Integer count = jdbcTemplate.queryForObject(QUERY_SEARCH_IF_EMAIL_EXISTS, Integer.class, email);
        return count != null && count > 0;
    }

    @Override
    public Boolean checkIfHasUsernameUsedByAnotherUser(String username) {
        Integer count = jdbcTemplate.queryForObject(QUERY_SEARCH_IF_USERNAME_EXISTS, Integer.class, username);
        return count != null && count > 0;
    }

    @Override
    public UUID generateUUIDCodeToCheckAccountMailVerification(RegisteredUserData client) {
        UUID uuid = UUID.randomUUID();
        int rowsUpdated = jdbcTemplate.update(QUERY_SAVE_VERIFY_CODE, uuid.toString(), client.getUsername());
        if (rowsUpdated == 0) {
            throw new RuntimeException("Error generating UUID code to verify account. UUID cannot be saved on database.");
        }
        return uuid;
    }

    @Override
    public void resetUserPassword(String username, String newPassword) {
        String encodedPassword = passwordEncoder.encode(newPassword);
        jdbcTemplate.update(QUERY_UPDATE_USER_PASSWORD, encodedPassword, username);
    }

    @Override
    public RecoveryPasswordCodeRequest saveCodeEmailRecoveryPassword(RegisteredUserData client, String code, RequestedBrowserParams requestedBrowserParams) {
        jdbcTemplate.update(QUERY_SAVE_RECOVERY_CODE_GENERATED_FOR_USER, code, client.getUsername(), requestedBrowserParams.getIpAddress(), requestedBrowserParams.getUserAgent(), LocalDateTime.now(), LocalDateTime.now().plusHours(24), Boolean.FALSE);
        return findCodeEmailRecoveryPassword(code, requestedBrowserParams);
    }

    @Override
    public RecoveryPasswordCodeRequest findCodeEmailRecoveryPassword(String code, RequestedBrowserParams requestedBrowserParams) {
        List<RecoveryPasswordCodeRequest> r = jdbcTemplate.query(QUERY_GET_RECOVERY_CODE_GENERATED_FOR_USER, new RecoveryPasswordCodeRequestMapper(), code, requestedBrowserParams.getIpAddress(), requestedBrowserParams.getUserAgent(), LocalDateTime.now());
        return r.stream().findFirst().orElseThrow(() -> new RuntimeException("Recovery code not found"));
    }

    @Override
    public RegisteredUserData saveUser(UserRegistrationData userRegistrationData, UserRolesEnum role) {
        userDetailsManager.createUser(User.withUsername(userRegistrationData.getUsername()).password(passwordEncoder.encode(userRegistrationData.getPassword())).roles(role.name()).build());
        jdbcTemplate.update(QUERY_SAVE_USER_DETAILS, userRegistrationData.getUsername(), userRegistrationData.getEmail(), userRegistrationData.getFirstName(), userRegistrationData.getLastName(), Boolean.FALSE);
        return getUserByUsername(userRegistrationData.getUsername());
    }

    @Override
    public RegisteredUserData getUserByUsername(String username) {
        List<RegisteredUserData> results = jdbcTemplate.query(QUERY_GET_BY_USERNAME, new RegisteredUserDataMapper(), username);
        return results.stream().findFirst().orElse(null);
    }

    @Override
    public RegisteredUserData getUserByEmail(String email) {
        List<RegisteredUserData> results = jdbcTemplate.query(QUERY_GET_BY_EMAIL, new RegisteredUserDataMapper(), email);
        return results.stream().findFirst().orElse(null);
    }

    @Override
    public Boolean verifyUserAccount(String uuidCode, RequestedBrowserParams browserParams) {
        List<String> results = jdbcTemplate.query(QUERY_GET_USERNAME_BY_UUID_CODE, (rs, rowNum) -> rs.getString("username"), uuidCode);
        String username = results.stream().findFirst().orElse(null);

        if (username == null) {
            throw new RuntimeException("User not found.");
        }
        int rowsUpdated = jdbcTemplate.update(QUERY_UPDATE_USER_DETAILS_VERIFIED, username);
        int rowsDeleted = jdbcTemplate.update(QUERY_DELETE_USER_VERIFY_CODE, uuidCode);
        return rowsUpdated > 0 && rowsDeleted > 0;
    }
}
