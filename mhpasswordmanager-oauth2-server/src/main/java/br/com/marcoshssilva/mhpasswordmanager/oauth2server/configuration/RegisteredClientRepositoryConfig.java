package br.com.marcoshssilva.mhpasswordmanager.oauth2server.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

import java.util.stream.Collectors;

import static br.com.marcoshssilva.mhpasswordmanager.oauth2server.configuration.OAuth2ConfigStarterConfig.registeredClientBuilder;

@Slf4j
@Configuration
public class RegisteredClientRepositoryConfig {

    @Bean("jdbcRegisteredClientRepository")
    @ConditionalOnProperty(name = "config.oauth.mode", havingValue = "DATABASE")
    public RegisteredClientRepository dbAuthJdbcRegisteredClientRepository(@Qualifier("dbAuthJdbcTemplate") JdbcTemplate jdbcTemplate) {
        log.info("RegisteredClientRepository using JdbcRegisteredClient with auth-db datasource");
        return new JdbcRegisteredClientRepository(jdbcTemplate);
    }

    @Bean("jdbcRegisteredClientRepository")
    @ConditionalOnProperty(name = "config.oauth.mode", havingValue = "EMBEDDED")
    public RegisteredClientRepository embeddedJdbcRegisteredClientRepository(@Qualifier("embeddedJdbcTemplate") JdbcTemplate jdbcTemplate) {
        log.info("RegisteredClientRepository using JdbcRegisteredClient with embedded datasource");
        return new JdbcRegisteredClientRepository(jdbcTemplate);
    }

    @Bean("inMemoryRegisteredClientRepository")
    @ConditionalOnProperty(name = "config.oauth.mode", havingValue = "MEMORY")
    public RegisteredClientRepository inMemoryRegisteredClientRepository(OAuth2ConfigStarterPropertiesConfig.OAuth2ConfigStarterProperties oAuth2ConfigStarterProperties, PasswordEncoder passwordEncoder) {
        log.info("RegisteredClientRepository using InMemoryRegisteredClient with default client and No Database");
        return new InMemoryRegisteredClientRepository(oAuth2ConfigStarterProperties.getClients().stream().map(client -> registeredClientBuilder(client, passwordEncoder).build()).collect(Collectors.toUnmodifiableList()));
    }
}
