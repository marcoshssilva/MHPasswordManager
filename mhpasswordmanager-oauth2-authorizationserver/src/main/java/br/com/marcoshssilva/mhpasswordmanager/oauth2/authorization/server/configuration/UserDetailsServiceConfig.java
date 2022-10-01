package br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

import javax.sql.DataSource;

@Configuration
public class UserDetailsServiceConfig {

    @Bean
    @Primary
    public UserDetailsService jdbcUserDetailsService(DataSource dataSource) {
        return new JdbcUserDetailsManager(dataSource);
    }

    public UserDetailsService inMemoryUserDetailsService(PasswordEncoder encoder) {
        UserDetails userDefault = User.builder()
                .username("user")
                .password(encoder.encode("user"))
                .roles("ADMIN", "USER", "MASTER")
                .build();

        return new InMemoryUserDetailsManager(userDefault);
    }
}
