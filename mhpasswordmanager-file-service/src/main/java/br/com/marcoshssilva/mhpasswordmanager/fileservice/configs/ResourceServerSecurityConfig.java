package br.com.marcoshssilva.mhpasswordmanager.fileservice.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class ResourceServerSecurityConfig {
    private static final String AUTHORITIES_CLAIM_NAME = "authorities";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/actuator/**").permitAll()
                .anyRequest().authenticated());
        http.oauth2ResourceServer()
                .jwt()
                .jwtAuthenticationConverter(jwtAuthenticationConverter());
        http.sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            List<String> userRoleAuthorities = null;
            JwtGrantedAuthoritiesConverter scopesConverter;
            Collection<GrantedAuthority> scopeAuthorities = null;

            try {
                userRoleAuthorities = jwt.getClaimAsStringList(AUTHORITIES_CLAIM_NAME);
            } catch (Exception e) {
                log.error("Cannot get AUTHORITIES FROM Token.", e);
            } finally {
                if (userRoleAuthorities == null) {
                    userRoleAuthorities = Collections.emptyList();
                }
            }

            try {
                scopesConverter = new JwtGrantedAuthoritiesConverter();
                scopeAuthorities = scopesConverter.convert(jwt);
            } catch (Exception e) {
                log.error("Cannot parse JWT Token.", e);
            } finally {
                if (scopeAuthorities == null) {
                    scopeAuthorities = Collections.emptyList();
                }
            }

            Collection<GrantedAuthority> authorities = new ArrayList<>(scopeAuthorities);
            authorities.addAll(userRoleAuthorities.stream()
                    .map(SimpleGrantedAuthority::new)
                    .toList());

            return authorities;
        });
        return converter;
    }
}