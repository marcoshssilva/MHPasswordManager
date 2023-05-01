package br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.configuration;

import br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.domain.constants.UserRolesEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

import javax.sql.DataSource;

@Configuration
public class UserDetailsServiceConfig {
    private static final Logger LOG = LoggerFactory.getLogger(UserDetailsServiceConfig.class);

    @Bean
    @Profile("in-memory-users")
    public UserDetailsManager inMemoryUserDetailsService(PasswordEncoder encoder) {
        final String logMessage = "UserDetailsService using InMemoryUserDetailsManager with default user: {} and password: {} - encrypted: {} - ROLES: {}";

        // user administrator
        final String user1 = "admin@mhpasswordmanager.com";
        final String pass1 = "P@ssword123";

        UserDetails userDefault = User.builder()
                .username(user1)
                .password(encoder.encode(pass1))
                .roles(UserRolesEnum.ADMIN.name(), UserRolesEnum.MASTER.name(), UserRolesEnum.USER.name())
                .build();

        LOG.info(logMessage, user1, pass1, userDefault.getPassword(), userDefault.getAuthorities());

        // user anonymous
        final String user2 = "anonymous@mhpasswordmanager.com";
        final String pass2 = "P@ssword123";

        UserDetails userAnonymous = User.builder()
                .username(user2)
                .password(encoder.encode(pass2))
                .roles(UserRolesEnum.USER.name())
                .build();

        LOG.info(logMessage, user2, pass2, userAnonymous.getPassword(), userAnonymous.getAuthorities());

        // save data
        return new InMemoryUserDetailsManager(userDefault, userAnonymous);
    }

    @Bean
    @Profile("!embedded-database & !in-memory-users")
    public UserDetailsManager jdbcUserDetailsManager(@Qualifier("dataSourceDbUsers") DataSource dataSource) {
        return new JdbcUserDetailsManager(dataSource);
    }

    @Bean
    @Profile("embedded-database")
    public UserDetailsManager jdbcUserDetailsManagerForEmbedded(@Qualifier("embeddedDatabase") DataSource dataSource) {
        return new JdbcUserDetailsManager(dataSource);
    }
}
