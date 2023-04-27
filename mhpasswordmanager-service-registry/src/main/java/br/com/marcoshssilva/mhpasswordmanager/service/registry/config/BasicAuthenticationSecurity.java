package br.com.marcoshssilva.mhpasswordmanager.service.registry.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
public class BasicAuthenticationSecurity {
    @Bean
    SecurityFilterChain basicAuthenticationSecurityFilterChainConfigurer(HttpSecurity http) throws Exception {
        return http
                .csrf().disable()
                .cors().disable()
                .authorizeRequests().anyRequest().authenticated()
                .and().httpBasic().and()
                .build();
    }
}
