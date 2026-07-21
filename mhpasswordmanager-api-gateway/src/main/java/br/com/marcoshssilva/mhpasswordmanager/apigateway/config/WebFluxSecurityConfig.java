package br.com.marcoshssilva.mhpasswordmanager.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class WebFluxSecurityConfig {
    private static final String SCOPE_GLOBAL_ACCESS     = "SCOPE_global:fullAccess";
    private static final String SCOPE_EUREKA_WRITE      = "SCOPE_eureka:write";
    private static final String SCOPE_EUREKA_READ       = "SCOPE_eureka:read";
    private static final String SCOPE_CONFIGSERVER_READ = "SCOPE_configserver:read";

    private static final String[] PUBLIC_GRANTED_ROUTES = {
            "/",
            "/index.html",
            "/favicon.ico",
            "/actuator/health/**",
            "/mypass-manager/auth/**",
            "/mypass-manager/auth/static/**",
            "/mypass-manager/auth/oauth2/**",
            "/mypass-manager/auth/error",
            "/mypass-manager/auth/login",
            "/mypass-manager/auth/v3/api-docs",
            "/mypass-manager/auth/v3/api-docs/swagger-config",
            "/mypass-manager/auth/swagger-ui.html",
            "/mypass-manager/auth/swagger-ui/**",
            "/mypass-manager/users/v3/api-docs",
            "/mypass-manager/users/v3/api-docs/swagger-config",
            "/mypass-manager/users/swagger-ui.html",
            "/mypass-manager/users/swagger-ui/**",
            "/mypass-manager/passwords/v3/api-docs",
            "/mypass-manager/passwords/v3/api-docs/swagger-config",
            "/mypass-manager/passwords/swagger-ui.html",
            "/mypass-manager/passwords/swagger-ui/**",
            "/mypass-manager/files/v3/api-docs",
            "/mypass-manager/files/v3/api-docs/swagger-config",
            "/mypass-manager/files/swagger-ui.html",
            "/mypass-manager/files/swagger-ui/**",
            "/mypass-manager/emails/v3/api-docs",
            "/mypass-manager/emails/v3/api-docs/swagger-config",
            "/mypass-manager/emails/swagger-ui.html",
            "/mypass-manager/emails/swagger-ui/**"
    };

    private static final String[] CONFIG_GRANTED_ROUTES = { "/config/**" };

    private static final String[] EUREKA_GRANTED_ROUTES = { "/eureka/**" };

    private static final String[] CONFIG_GRANTED_SCOPES = { SCOPE_GLOBAL_ACCESS, SCOPE_CONFIGSERVER_READ };

    private static final String[] EUREKA_GRANTED_SCOPES_WRITE = { SCOPE_GLOBAL_ACCESS, SCOPE_EUREKA_WRITE };

    private static final String[] EUREKA_GRANTED_SCOPES_READ = { SCOPE_GLOBAL_ACCESS, SCOPE_EUREKA_READ };

    @Bean
    SecurityWebFilterChain enableOAuthResourceServerSecurity(ServerHttpSecurity http) {
        http.authorizeExchange(
                exchange -> exchange
                        .pathMatchers(PUBLIC_GRANTED_ROUTES).permitAll()
                        .pathMatchers(HttpMethod.GET,    EUREKA_GRANTED_ROUTES).hasAnyAuthority(EUREKA_GRANTED_SCOPES_READ)
                        .pathMatchers(HttpMethod.POST,   EUREKA_GRANTED_ROUTES).hasAnyAuthority(EUREKA_GRANTED_SCOPES_WRITE)
                        .pathMatchers(HttpMethod.PUT,    EUREKA_GRANTED_ROUTES).hasAnyAuthority(EUREKA_GRANTED_SCOPES_WRITE)
                        .pathMatchers(HttpMethod.DELETE, EUREKA_GRANTED_ROUTES).hasAnyAuthority(EUREKA_GRANTED_SCOPES_WRITE)
                        .pathMatchers(HttpMethod.GET,    CONFIG_GRANTED_ROUTES).hasAnyAuthority(CONFIG_GRANTED_SCOPES)
                        .anyExchange().authenticated()
                )
            .oauth2ResourceServer()
            .jwt();

        // REQUIRED BECAUSE IS ENABLED BY OAUTH2-SERVER, prefer enable CSRF in API directly
        return http.csrf().disable().build();
    }
}
