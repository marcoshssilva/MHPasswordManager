package br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final String[] IGNORED_CSRF_ROUTES = new String[]{"/h2/**"};

    private final String[] PUBLIC_ROUTES = new String[]{"/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/content/**", "/actuator/**"};

    private final String[] GET_METHOD_ONLY_PUBLIC = new String[] {"/forgotPassword", "/"};

    private final String[] POST_METHOD_ONLY_PUBLIC = new String[] {"/api/account/register"};

    @Order(1)
    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        // csrf for specific routes
        http.csrf().ignoringAntMatchers(IGNORED_CSRF_ROUTES).csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
        // allow frame options only to sameOrigin -> fix /h2
        http.headers().frameOptions().sameOrigin();
        // config security routes
        http.authorizeHttpRequests((authorize) -> authorize
                // register public routes
                .antMatchers(PUBLIC_ROUTES).permitAll()
                // register available path -> /h2 <- only to ROLE_ADMIN and ROLE_MASTER
                .antMatchers("/h2/**").hasAnyRole("ADMIN", "MASTER")
                // register as public access only when GET method
                .antMatchers(HttpMethod.GET, GET_METHOD_ONLY_PUBLIC).permitAll()
                // register as public access only when POST method
                .antMatchers(HttpMethod.POST, POST_METHOD_ONLY_PUBLIC).permitAll()
                // any other requests for authenticated
                .anyRequest().authenticated()
        );

        return http
                // don't need to add in PUBLIC ROUTES because this method make it from default
                .formLogin(login -> login.loginPage("/login").permitAll())
                .logout(logout -> logout.logoutUrl("/logout").permitAll())
                .build();
    }
}
