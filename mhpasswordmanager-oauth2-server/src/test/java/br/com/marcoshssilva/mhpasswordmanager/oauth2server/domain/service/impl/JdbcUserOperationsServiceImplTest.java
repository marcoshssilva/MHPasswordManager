package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.impl;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.RegisteredUserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.*;

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
        
        // Adicionar tabelas que faltam no schema.sql original
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS users_recovery_password_code (code VARCHAR(11) NOT NULL PRIMARY KEY, username VARCHAR(255) NOT NULL, ip_client VARCHAR(50) NOT NULL, user_agent_client VARCHAR(255) NOT NULL, created_at timestamp NOT NULL, expires_at timestamp NOT NULL, completed boolean NOT NULL)");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS users_verify_codes (uuid_code VARCHAR(36) NOT NULL PRIMARY KEY, username VARCHAR(255) NOT NULL)");
        
        // Corrigir/Aumentar users_details se necessário (o schema.sql original pode estar incompleto)
        jdbcTemplate.execute("DROP TABLE users_details");
        jdbcTemplate.execute("CREATE TABLE users_details (username varchar(255) NOT NULL PRIMARY KEY, email varchar(255), firstname varchar(255), lastname varchar(255), verified boolean, verified_at timestamp, imageUrl varchar(255))");

        service = new JdbcUserOperationsServiceImpl(passwordEncoder, jdbcTemplate);
    }

    @Test
    void shouldReturnFalseWhenEmailDoesNotExist() {
        // Este deve falhar com EmptyResultDataAccessException se o erro persistir
        assertFalse(service.checkIfHasEmailUsedByAnotherUser("nonexistent@example.com"));
    }

    @Test
    void shouldReturnFalseWhenUsernameDoesNotExist() {
        // Este deve falhar com EmptyResultDataAccessException se o erro persistir
        assertFalse(service.checkIfHasUsernameUsedByAnotherUser("nonexistentuser"));
    }

    @Test
    void shouldReturnNullOrThrowSpecificExceptionWhenUserNotFoundByUsername() {
        // Agora retorna null se não encontrado
        assertNull(service.getUserByUsername("nonexistentuser"));
    }

    @Test
    void shouldReturnTrueWhenEmailExists() {
        jdbcTemplate.execute("INSERT INTO users (username, password, enabled) VALUES ('user1', 'pass', true)");
        jdbcTemplate.execute("INSERT INTO users_details (username, email, firstname, lastname, verified) VALUES ('user1', 'user1@example.com', 'First', 'Last', true)");
        
        assertTrue(service.checkIfHasEmailUsedByAnotherUser("user1@example.com"));
    }

    @Test
    void shouldReturnTrueWhenUsernameExists() {
        jdbcTemplate.execute("INSERT INTO users (username, password, enabled) VALUES ('user1', 'pass', true)");
        
        assertTrue(service.checkIfHasUsernameUsedByAnotherUser("user1"));
    }

    @Test
    void shouldReturnUserDataWhenUserExistsByUsername() {
        jdbcTemplate.execute("INSERT INTO users (username, password, enabled) VALUES ('user1', 'pass', true)");
        jdbcTemplate.execute("INSERT INTO users_details (username, email, firstname, lastname, verified) VALUES ('user1', 'user1@example.com', 'First', 'Last', true)");
        
        RegisteredUserData data = service.getUserByUsername("user1");
        assertNotNull(data);
        assertEquals("user1", data.getUsername());
        assertEquals("user1@example.com", data.getEmail());
    }

    @Test
    void shouldReturnUserDataWhenUserExistsByEmail() {
        jdbcTemplate.execute("INSERT INTO users (username, password, enabled) VALUES ('user1', 'pass', true)");
        jdbcTemplate.execute("INSERT INTO users_details (username, email, firstname, lastname, verified) VALUES ('user1', 'user1@example.com', 'First', 'Last', true)");
        
        RegisteredUserData data = service.getUserByEmail("user1@example.com");
        assertNotNull(data);
        assertEquals("user1", data.getUsername());
        assertEquals("user1@example.com", data.getEmail());
    }
}
