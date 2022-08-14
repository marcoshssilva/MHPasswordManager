package br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
public class SecurityConfig {

    @Order(1)
    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests((authorize) -> {
            try {
                authorize.antMatchers("/h2/**").permitAll().and().csrf().disable().headers().frameOptions().disable();
                authorize.anyRequest().authenticated();
            } catch (Exception e) {
                throw new RuntimeException("Cannot configure HttpSecurity.", e);
            }
        });

        http.formLogin(Customizer.withDefaults());
        return http.build();
    }

}
