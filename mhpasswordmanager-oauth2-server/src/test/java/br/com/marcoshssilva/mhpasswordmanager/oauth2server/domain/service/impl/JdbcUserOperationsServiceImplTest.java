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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JdbcUserOperationsServiceImplTest {

    private JdbcUserOperationsServiceImpl service;
    private JdbcTemplate jdbcTemplate;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder(10);
        DataSource dataSource = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .setName("testdb_" + System.currentTimeMillis())
                .setScriptEncoding("UTF-8")
                .addScript("org/springframework/security/oauth2/server/authorization/oauth2-authorization-schema.sql")
                .addScript("org/springframework/security/oauth2/server/authorization/oauth2-authorization-consent-schema.sql")
                .addScript("org/springframework/security/oauth2/server/authorization/client/oauth2-registered-client-schema.sql")
                .addScript("db/h2/schema.sql")
                .addScript("db/h2/data.sql")
                .build();

        jdbcTemplate = new JdbcTemplate(dataSource);

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS users_recovery_password_code (code VARCHAR(11) NOT NULL PRIMARY KEY, username VARCHAR(255) NOT NULL, ip_client VARCHAR(50) NOT NULL, user_agent_client VARCHAR(255) NOT NULL, created_at timestamp NOT NULL, expires_at timestamp NOT NULL, completed boolean NOT NULL)");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS users_verify_codes (uuid_code VARCHAR(36) NOT NULL PRIMARY KEY, username VARCHAR(255) NOT NULL)");
        jdbcTemplate.execute("DROP TABLE users_details");
        jdbcTemplate.execute("CREATE TABLE users_details (username varchar(255) NOT NULL PRIMARY KEY, email varchar(255), firstname varchar(255), lastname varchar(255), verified boolean, verified_at timestamp, imageUrl varchar(255))");

        service = new JdbcUserOperationsServiceImpl(passwordEncoder, jdbcTemplate);
    }

    @DisplayName("Should return false when email does not exist")
    @Test
    void shouldReturnFalseWhenEmailDoesNotExist() {
        assertFalse(service.checkIfHasEmailUsedByAnotherUser("nonexistent@example.com"));
    }

    @DisplayName("Should return false when username does not exist")
    @Test
    void shouldReturnFalseWhenUsernameDoesNotExist() {
        assertFalse(service.checkIfHasUsernameUsedByAnotherUser("nonexistentuser"));
    }

    @DisplayName("Should return null when user not found by username")
    @Test
    void shouldReturnNullWhenUserNotFoundByUsername() {
        assertNull(service.getUserByUsername("nonexistentuser"));
    }

    @DisplayName("Should return true when email exists")
    @Test
    void shouldReturnTrueWhenEmailExists() {
        jdbcTemplate.execute("INSERT INTO users (username, password, enabled) VALUES ('user1', 'pass', true)");
        jdbcTemplate.execute("INSERT INTO users_details (username, email, firstname, lastname, verified) VALUES ('user1', 'user1@example.com', 'First', 'Last', true)");

        assertTrue(service.checkIfHasEmailUsedByAnotherUser("user1@example.com"));
    }

    @DisplayName("Should return true when username exists")
    @Test
    void shouldReturnTrueWhenUsernameExists() {
        jdbcTemplate.execute("INSERT INTO users (username, password, enabled) VALUES ('user1', 'pass', true)");

        assertTrue(service.checkIfHasUsernameUsedByAnotherUser("user1"));
    }

    @DisplayName("Should return user data when user exists by username")
    @Test
    void shouldReturnUserDataWhenUserExistsByUsername() {
        jdbcTemplate.execute("INSERT INTO users (username, password, enabled) VALUES ('user1', 'pass', true)");
        jdbcTemplate.execute("INSERT INTO users_details (username, email, firstname, lastname, verified) VALUES ('user1', 'user1@example.com', 'First', 'Last', true)");

        RegisteredUserData data = service.getUserByUsername("user1");
        assertNotNull(data);
        assertEquals("user1", data.getUsername());
        assertEquals("user1@example.com", data.getEmail());
    }

    @DisplayName("Should return user data when user exists by email")
    @Test
    void shouldReturnUserDataWhenUserExistsByEmail() {
        jdbcTemplate.execute("INSERT INTO users (username, password, enabled) VALUES ('user1', 'pass', true)");
        jdbcTemplate.execute("INSERT INTO users_details (username, email, firstname, lastname, verified) VALUES ('user1', 'user1@example.com', 'First', 'Last', true)");

        RegisteredUserData data = service.getUserByEmail("user1@example.com");
        assertNotNull(data);
        assertEquals("user1", data.getUsername());
        assertEquals("user1@example.com", data.getEmail());
    }

    @DisplayName("Should save user and retrieve successfully")
    @Test
    void shouldSaveUserSuccessfully() {
        UserRegistrationData regData = UserRegistrationData.builder()
                .username("newuser")
                .email("newuser@example.com")
                .firstName("New")
                .lastName("User")
                .password("Password123")
                .confirmationPassword("Password123")
                .build();

        RegisteredUserData saved = service.saveUser(regData, UserRolesEnum.USER);
        assertNotNull(saved);
        assertEquals("newuser", saved.getUsername());
        assertEquals("newuser@example.com", saved.getEmail());
        assertEquals("New", saved.getFirstName());
        assertEquals("User", saved.getLastName());
    }

    @DisplayName("Should reset user password successfully")
    @Test
    void shouldResetUserPasswordSuccessfully() {
        jdbcTemplate.execute("INSERT INTO users (username, password, enabled) VALUES ('user_reset', 'oldpass', true)");

        service.resetUserPassword("user_reset", "newpass123");

        String encoded = jdbcTemplate.queryForObject("SELECT password FROM users WHERE username = 'user_reset'", String.class);
        assertNotNull(encoded);
        assertTrue(passwordEncoder.matches("newpass123", encoded));
    }

    @DisplayName("Should generate UUID verification code successfully")
    @Test
    void shouldGenerateUUIDVerificationCodeSuccessfully() {
        RegisteredUserData user = RegisteredUserData.builder().username("verify_user").build();

        UUID uuid = service.generateUUIDCodeToCheckAccountMailVerification(user);
        assertNotNull(uuid);

        String username = jdbcTemplate.queryForObject("SELECT username FROM users_verify_codes WHERE uuid_code = ?", String.class, uuid.toString());
        assertEquals("verify_user", username);
    }

    @DisplayName("Should save and find recovery password code successfully")
    @Test
    void shouldSaveAndFindRecoveryPasswordCodeSuccessfully() {
        RegisteredUserData user = RegisteredUserData.builder().username("recovery_user").build();
        RequestedBrowserParams browserParams = RequestedBrowserParams.builder()
                .ipAddress("127.0.0.1")
                .userAgent("Mozilla")
                .build();

        RecoveryPasswordCodeRequest saved = service.saveCodeEmailRecoveryPassword(user, "12345678901", browserParams);
        assertNotNull(saved);
        assertEquals("12345678901", saved.getCode());
        assertEquals("recovery_user", saved.getUsername());

        RecoveryPasswordCodeRequest found = service.findCodeEmailRecoveryPassword("12345678901", browserParams);
        assertNotNull(found);
        assertEquals("12345678901", found.getCode());
    }

    @DisplayName("Should throw exception when recovery password code not found")
    @Test
    void shouldThrowExceptionWhenRecoveryCodeNotFound() {
        RequestedBrowserParams browserParams = RequestedBrowserParams.builder()
                .ipAddress("127.0.0.1")
                .userAgent("Mozilla")
                .build();

        assertThrows(RuntimeException.class, () -> service.findCodeEmailRecoveryPassword("nonexistent", browserParams));
    }

    @DisplayName("Should verify user account successfully")
    @Test
    void shouldVerifyUserAccountSuccessfully() {
        jdbcTemplate.execute("INSERT INTO users (username, password, enabled) VALUES ('user_verify', 'pass', true)");
        jdbcTemplate.execute("INSERT INTO users_details (username, email, firstname, lastname, verified) VALUES ('user_verify', 'uv@example.com', 'U', 'V', false)");
        jdbcTemplate.execute("INSERT INTO users_verify_codes (uuid_code, username) VALUES ('code-12345', 'user_verify')");

        RequestedBrowserParams browserParams = RequestedBrowserParams.builder().build();
        Boolean verified = service.verifyUserAccount("code-12345", browserParams);
        assertTrue(verified);

        Boolean isVerified = jdbcTemplate.queryForObject("SELECT verified FROM users_details WHERE username = 'user_verify'", Boolean.class);
        assertEquals(Boolean.TRUE, isVerified);

        Integer countCodes = jdbcTemplate.queryForObject("SELECT count(1) FROM users_verify_codes WHERE uuid_code = 'code-12345'", Integer.class);
        assertEquals(0, countCodes);
    }

    @DisplayName("Should throw UserOperationErrorException when verifying non-existent uuid code")
    @Test
    void shouldThrowExceptionWhenVerifyingNonExistentCode() {
        RequestedBrowserParams browserParams = RequestedBrowserParams.builder().build();
        assertThrows(UserOperationErrorException.class, () -> service.verifyUserAccount("unknown-code", browserParams));
    }
}
