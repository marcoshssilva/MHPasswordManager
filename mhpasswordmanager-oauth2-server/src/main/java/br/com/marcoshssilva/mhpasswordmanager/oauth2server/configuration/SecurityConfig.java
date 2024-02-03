package br.com.marcoshssilva.mhpasswordmanager.oauth2server.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@lombok.RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {
    private final AuthorizationConfigProperties authorizationConfigProperties;

    public final String[] publicRoutes = new String[]{"/h2/**", "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/content/**", "/actuator/**"};

    private final String[] getMethodOnlyPublic = new String[]{"/verify/**"};

    private final String[] postMethodOnlyPublic = new String[]{"/api/account/register", "/api/account/forgot/step1", "/api/account/forgot/step2"};

    @Order(1)
    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        // csrf for specific routes
        http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
        // allow frame options only to sameOrigin -> fix /h2
        http.headers().frameOptions().sameOrigin();
        // config security routes
        http.authorizeHttpRequests(authorize -> authorize
                // register public routes
                .antMatchers(publicRoutes).permitAll()
                // register as public access only when GET method
                .antMatchers(HttpMethod.GET, getMethodOnlyPublic).permitAll()
                // register as public access only when POST method
                .antMatchers(HttpMethod.POST, postMethodOnlyPublic).permitAll()
                // any other requests for authenticated
                .anyRequest().authenticated()
        );

        return http
                // don't need to add in PUBLIC ROUTES because this method make it from default
                .formLogin(login -> login.loginPage("/login").permitAll())
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                        .logoutSuccessUrl(this.authorizationConfigProperties.getSuccessLogoutUri())
                        .permitAll())
                .build();
    }
}
