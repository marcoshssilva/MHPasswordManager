package br.com.marcoshssilva.mhpasswordmanager.serviceregistry.config;

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
    protected final String[] endpointsIgnoreCsrfAttack = new String[] { "/eureka", "/eureka/**" };
    protected final String[] endpointsIgnoreAuthentication = new String[] { "/actuator/**" };
    protected final String[] endpointsNeedAuthentication = new String[] { "/eureka/**" };

    @Bean
    SecurityFilterChain basicAuthenticationSecurityFilterChainConfigurer(HttpSecurity http) throws Exception {
        return http
                .csrf()
                    .ignoringAntMatchers(endpointsIgnoreCsrfAttack)
                .and()
                .authorizeRequests()
                    .antMatchers(endpointsIgnoreAuthentication).permitAll()
                    .antMatchers(endpointsNeedAuthentication).authenticated()
                    .anyRequest().authenticated().and()
                .httpBasic()
                .and()
                .build();
    }
}
