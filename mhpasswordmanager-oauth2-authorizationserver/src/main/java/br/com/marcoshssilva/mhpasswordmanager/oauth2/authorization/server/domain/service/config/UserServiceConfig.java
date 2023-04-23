package br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.domain.service.config;

import br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.domain.service.UserService;
import br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.domain.service.impl.InMemoryUserServiceImpl;
import br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.domain.service.impl.JdbcUserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;

@Configuration
public class UserServiceConfig {
    @Bean
    @Profile("!embedded-database & !in-memory-users")
    public UserService inJdbcUserService(@Autowired UserDetailsManager userDetailsManager, @Autowired PasswordEncoder passwordEncoder, @Autowired @Qualifier("dbUsersJdbcTemplate") JdbcTemplate jdbcTemplate) {
        return new JdbcUserServiceImpl(userDetailsManager, passwordEncoder, jdbcTemplate);
    }

    @Bean
    @Profile("embedded-database")
    public UserService inEmbeddedJdbcUserService(@Autowired UserDetailsManager userDetailsManager, @Autowired PasswordEncoder passwordEncoder, @Autowired @Qualifier("embeddedJdbcTemplate") JdbcTemplate jdbcTemplate) {
        return new JdbcUserServiceImpl(userDetailsManager, passwordEncoder, jdbcTemplate);
    }

    @Bean
    @Profile("in-memory-users")
    public UserService inMemoryUserService(@Autowired UserDetailsManager userDetailsManager, @Autowired PasswordEncoder passwordEncoder) {
        return new InMemoryUserServiceImpl(userDetailsManager, passwordEncoder);
    }
}
