package br.com.marcoshssilva.mhpasswordmanager.userservice.configs;

import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.repositories.UserHasRoleRepository;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.repositories.UserRepository;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.services.JpaUserService;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.services.JpaUserServiceImpl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JpaUserServiceConfig {

    @Bean
    JpaUserService jpaUserService(UserRepository userRepository, UserHasRoleRepository userHasRoleRepository) {
        return new JpaUserServiceImpl(userRepository, userHasRoleRepository);
    }
}
