package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.config;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.configuration.AuthorizationConfigProperties;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.SendEmailService;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.UserOperationsService;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.UserService;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.clients.web.UserServiceWebClient;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.impl.InMemoryUserOperationsServiceImpl;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.impl.JdbcUserOperationsServiceImpl;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.impl.UserServiceImpl;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.impl.WebClientUserOperationsServiceImpl;

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
    @Bean
    @ConditionalOnProperty(name = "config.users.mode", havingValue = "DATABASE")
    @ConditionalOnClass(JdbcTemplate.class)
    @Primary
    public UserOperationsService inJdbcUserService(@Autowired PasswordEncoder passwordEncoder, @Autowired @Qualifier("dbUsersJdbcTemplate") JdbcTemplate jdbcTemplate) {
        return new JdbcUserOperationsServiceImpl(passwordEncoder, jdbcTemplate);
    }

    @Bean
    @ConditionalOnProperty(name = "config.users.mode", havingValue = "MEMORY")
    public UserOperationsService inMemoryUserService(@Autowired UserDetailsManager userDetailsManager, @Autowired PasswordEncoder passwordEncoder) {
        return new InMemoryUserOperationsServiceImpl(userDetailsManager, passwordEncoder);
    }

    @Bean
    @ConditionalOnProperty(name = "config.users.mode", havingValue = "WEB_CLIENT")
    public UserOperationsService inWebClientUserService(@Autowired UserServiceWebClient userServiceWebClient) {
        return new WebClientUserOperationsServiceImpl(userServiceWebClient);
    }

    @Bean
    public UserService userService(@Autowired UserOperationsService userOperationsService, @Autowired SendEmailService sendEmailService, @Autowired AuthorizationConfigProperties authorizationConfigProperties) {
        return new UserServiceImpl(userOperationsService, sendEmailService, authorizationConfigProperties);
    }
}
