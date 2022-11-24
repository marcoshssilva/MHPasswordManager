package br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final String[] IGNORED_CSRF_ROUTES = new String[]{"/h2/**"};

    private final String[] PUBLIC_ROUTES = new String[]{};

    @Order(1)
    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        // csrf for specific routes
        http.csrf().ignoringAntMatchers(IGNORED_CSRF_ROUTES);
        // allow frame options only to sameOrigin -> fix /h2
        http.headers().frameOptions().sameOrigin();
        // config security routes
        http.authorizeHttpRequests((authorize) -> authorize
                // register public routes
                .antMatchers(PUBLIC_ROUTES).permitAll()
                // register available path -> /h2 <- only to ROLE_ADMIN and ROLE_MASTER
                .antMatchers("/h2/**").hasAnyRole("ADMIN", "MASTER")
                // any other requests for authenticated
                .anyRequest().authenticated()
        );

        return http
                .formLogin(Customizer.withDefaults())
                .build();
    }
}
