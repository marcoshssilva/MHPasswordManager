package br.com.marcoshssilva.mhpasswordmanager.serviceregistry.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
public class BasicAuthenticationSecurity {
    protected final String[] endpointsIgnoreAuthentication = new String[] { "/actuator/health/**" };
    protected final String[] endpointsNeedAuthentication = new String[] { "/eureka/**" };

    @Bean
    SecurityFilterChain basicAuthenticationSecurityFilterChainConfigurer(HttpSecurity http) throws Exception {
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.httpBasic();
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(endpointsIgnoreAuthentication).permitAll()
                .requestMatchers(endpointsNeedAuthentication).authenticated()
                .anyRequest().authenticated());

        return http.build();
    }
}
