package br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.configuration;

import br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.domain.constants.UserRolesEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

import javax.sql.DataSource;

@Configuration
public class UserDetailsServiceConfig {
    private static final Logger LOG = LoggerFactory.getLogger(UserDetailsServiceConfig.class);

    @Bean
    @Profile("!test")
    @Primary
    public UserDetailsService jdbcUserDetailsService(DataSource dataSource) {
        return new JdbcUserDetailsManager(dataSource);
    }

    @Bean
    @Profile("test")
    public UserDetailsService inMemoryUserDetailsService(PasswordEncoder encoder) {
        final String user = "admin";
        final String pass = "42e5cdb0-600f-4814-b4a9-08cfd76d87a6";
        final String logMessage = "UserDetailsService using InMemoryUserDetailsManager with default user: {} and password: {}";

        UserDetails userDefault = User.builder()
                .username(user)
                .password(encoder.encode(pass))
                .roles(UserRolesEnum.ADMIN.name(), UserRolesEnum.MASTER.name(), UserRolesEnum.USER.name())
                .build();

        LOG.info(logMessage, user, pass);
        return new InMemoryUserDetailsManager(userDefault);
    }
}
