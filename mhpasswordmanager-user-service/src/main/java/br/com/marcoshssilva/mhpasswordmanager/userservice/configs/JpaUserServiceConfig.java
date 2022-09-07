package br.com.marcoshssilva.mhpasswordmanager.userservice.configs;

import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.repositories.UserRepository;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.services.UserService;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.services.JpaUserServiceImpl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JpaUserServiceConfig {

    @Bean
    UserService jpaUserService(UserRepository userRepository) {
        return new JpaUserServiceImpl(userRepository);
    }
}
