package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.config;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.configuration.AuthorizationConfigProperties;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.SendEmailService;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.UserService;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.clients.web.UserServiceWebClient;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.impl.InMemoryUserServiceImpl;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.impl.JdbcUserServiceImpl;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.impl.WebClientUserServiceImpl;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class UserServiceConfig {
    private final RabbitTemplate rabbitTemplate;
    private final AuthorizationConfigProperties authorizationConfigProperties;
    private final SendEmailService sendEmailService;

    @Bean
    @ConditionalOnProperty(name = "config.users.mode", havingValue = "DATABASE")
    @ConditionalOnClass(JdbcTemplate.class)
    @Primary
    public UserService inJdbcUserService(@Autowired UserDetailsManager userDetailsManager, @Autowired PasswordEncoder passwordEncoder, @Autowired @Qualifier("dbUsersJdbcTemplate") JdbcTemplate jdbcTemplate) {
        return new JdbcUserServiceImpl(userDetailsManager, passwordEncoder, jdbcTemplate, rabbitTemplate, authorizationConfigProperties, sendEmailService);
    }

    @Bean
    @ConditionalOnProperty(name = "config.users.mode", havingValue = "MEMORY")
    public UserService inMemoryUserService(@Autowired UserDetailsManager userDetailsManager, @Autowired PasswordEncoder passwordEncoder) {
        return new InMemoryUserServiceImpl(userDetailsManager, passwordEncoder);
    }

    @Bean
    @ConditionalOnProperty(name = "config.users.mode", havingValue = "WEB_CLIENT")
    public UserService inWebClientUserService(@Autowired UserDetailsManager userDetailsManager, @Autowired PasswordEncoder passwordEncoder, @Autowired UserServiceWebClient userServiceWebClient) {
        return new WebClientUserServiceImpl(userDetailsManager, passwordEncoder, userServiceWebClient);
    }
}
