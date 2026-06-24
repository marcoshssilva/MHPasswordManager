package br.com.marcoshssilva.mhpasswordmanager.oauth2server.configuration;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.impl.CustomWebClientUserDetailsManagerImpl;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.clients.web.UserServiceWebClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

import javax.sql.DataSource;

@lombok.extern.slf4j.Slf4j
@Configuration
public class UserDetailsServiceConfig {

    @Bean
    @ConditionalOnProperty(name = "config.users.mode", havingValue = "WEB_CLIENT")
    public CustomWebClientUserDetailsManagerImpl webClientUserDetailsManager(UserServiceWebClient userServiceWebClient) {
        log.info("UserDetailsManager using WebClient with user-service API");
        return new CustomWebClientUserDetailsManagerImpl(userServiceWebClient);
    }

    @Bean
    @ConditionalOnProperty(name = "config.users.mode", havingValue = "MEMORY")
    public UserDetailsManager inMemoryUserDetailsService() {
        log.info("UserDetailsManager using InMemoryUserDetailsManager with default users from application.properties");
        return new InMemoryUserDetailsManager();
    }

    @Bean
    @ConditionalOnProperty(name = "config.users.mode", havingValue = "EMBEDDED")
    public UserDetailsManager jdbcUserDetailsManagerForEmbedded(@Qualifier("embeddedDatabaseDataSource") DataSource dataSource) {
        log.info("UserDetailsManager using embedded JdbcUserDetailsManager");
        return new JdbcUserDetailsManager(dataSource);
    }

    @Bean
    @ConditionalOnProperty(name = "config.users.mode", havingValue = "DATABASE")
    public UserDetailsManager jdbcUserDetailsManagerForDbUsers(@Qualifier("dbUsersDatabaseDataSource") DataSource dataSource) {
        log.info("UserDetailsManager using database JdbcUserDetailsManager");
        return new JdbcUserDetailsManager(dataSource);
    }
}
