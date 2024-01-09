package br.com.marcoshssilva.mhpasswordmanager.oauth2server.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

@Slf4j
@Configuration
public class RegisteredClientRepositoryConfig {

    @Bean
    @Profile("!in-memory-client & !embedded-database")
    public RegisteredClientRepository dbAuthJdbcRegisteredClientRepository(@Qualifier("dbAuthJdbcTemplate") JdbcTemplate jdbcTemplate) {
        log.info("RegisteredClientRepository using JdbcRegisteredClient with auth-db datasource");
        return new JdbcRegisteredClientRepository(jdbcTemplate);
    }

    @Bean
    @Profile("embedded-database")
    public RegisteredClientRepository embeddedJdbcRegisteredClientRepository(@Qualifier("embeddedJdbcTemplate") JdbcTemplate jdbcTemplate) {
        log.info("RegisteredClientRepository using JdbcRegisteredClient with embedded-database");
        return new JdbcRegisteredClientRepository(jdbcTemplate);
    }

    @Bean
    @Profile("in-memory-client")
    public RegisteredClientRepository inMemoryRegisteredClientRepository() {
        log.info("RegisteredClientRepository using InMemoryRegisteredClient with default client and No Database");
        return new InMemoryRegisteredClientRepository();
    }
}
