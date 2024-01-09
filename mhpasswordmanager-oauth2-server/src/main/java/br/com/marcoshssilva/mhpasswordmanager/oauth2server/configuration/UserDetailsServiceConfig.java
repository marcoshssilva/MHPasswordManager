package br.com.marcoshssilva.mhpasswordmanager.oauth2server.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

import javax.sql.DataSource;

@Slf4j
@Configuration
public class UserDetailsServiceConfig {
    @Bean
    @Profile("in-memory-users")
    public UserDetailsManager inMemoryUserDetailsService() {
        log.info("UserDetailsManager using InMemoryUserDetailsManager with default users and No Database");
        return new InMemoryUserDetailsManager();
    }

    @Bean
    @Profile("!embedded-database & !in-memory-users")
    public UserDetailsManager jdbcUserDetailsManager(@Qualifier("dataSourceDbUsers") DataSource dataSource) {
        log.info("UserDetailsManager using JdbcUserDetailsManager with users-db datasource");
        return new JdbcUserDetailsManager(dataSource);
    }

    @Bean
    @Profile("embedded-database")
    public UserDetailsManager jdbcUserDetailsManagerForEmbedded(@Qualifier("embeddedDatabase") DataSource dataSource) {
        log.info("UserDetailsManager using JdbcUserDetailsManager with embedded-database");
        return new JdbcUserDetailsManager(dataSource);
    }
}
