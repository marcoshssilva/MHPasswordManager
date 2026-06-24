package br.com.marcoshssilva.mhpasswordmanager.oauth2server.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@lombok.RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {
    private final AuthorizationConfigProperties authorizationConfigProperties;

    public final String[] publicRoutes = new String[]{"/h2/**", "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/content/**", "/actuator/**"};

    private final String[] getMethodOnlyPublic = new String[]{"/verify/**"};

    private final String[] postMethodOnlyPublic = new String[]{"/api/account/register", "/api/account/forgot/step1", "/api/account/forgot/step2"};

    private final String[] enabledOnlyToAdmin = new String[] { "/api/jwk-management/**" };

    @Order(1)
    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {

        http.csrf().csrfTokenRepository(new CookieCsrfTokenRepository());
        http.headers().frameOptions().sameOrigin();
        http.formLogin(login -> login.loginPage("/login").permitAll());
        http.logout(logout -> logout.logoutUrl("/logout").permitAll());

        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(publicRoutes).permitAll()
                .requestMatchers(HttpMethod.GET, getMethodOnlyPublic).permitAll()
                .requestMatchers(HttpMethod.POST, postMethodOnlyPublic).permitAll()
                .requestMatchers(enabledOnlyToAdmin).hasAnyRole("ADMIN")
                .anyRequest().authenticated());

        return http.build();
    }
}
