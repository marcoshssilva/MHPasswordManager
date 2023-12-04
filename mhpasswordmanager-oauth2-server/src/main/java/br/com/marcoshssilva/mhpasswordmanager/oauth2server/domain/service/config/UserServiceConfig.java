package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.config;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.configuration.AuthorizationConfigProperties;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.SendEmailService;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.UserService;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.impl.InMemoryUserServiceImpl;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.impl.JdbcUserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;

@Configuration
@RequiredArgsConstructor
public class UserServiceConfig {
    private final RabbitTemplate rabbitTemplate;
    private final AuthorizationConfigProperties authorizationConfigProperties;
    private final SendEmailService sendEmailService;

    @Bean
    @Profile("!embedded-database & !in-memory-users")
    public UserService inJdbcUserService(@Autowired UserDetailsManager userDetailsManager, @Autowired PasswordEncoder passwordEncoder, @Autowired @Qualifier("dbUsersJdbcTemplate") JdbcTemplate jdbcTemplate) {
        return new JdbcUserServiceImpl(userDetailsManager, passwordEncoder, jdbcTemplate, rabbitTemplate, authorizationConfigProperties, sendEmailService);
    }

    @Bean
    @Profile("embedded-database")
    public UserService inEmbeddedJdbcUserService(@Autowired UserDetailsManager userDetailsManager, @Autowired PasswordEncoder passwordEncoder, @Autowired @Qualifier("embeddedJdbcTemplate") JdbcTemplate jdbcTemplate) {
        return new JdbcUserServiceImpl(userDetailsManager, passwordEncoder, jdbcTemplate, rabbitTemplate, authorizationConfigProperties, sendEmailService);
    }

    @Bean
    @Profile("in-memory-users")
    public UserService inMemoryUserService(@Autowired UserDetailsManager userDetailsManager, @Autowired PasswordEncoder passwordEncoder) {
        return new InMemoryUserServiceImpl(userDetailsManager, passwordEncoder);
    }
}
