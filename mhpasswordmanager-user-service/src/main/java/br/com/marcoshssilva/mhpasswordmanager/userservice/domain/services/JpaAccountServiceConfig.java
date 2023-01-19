package br.com.marcoshssilva.mhpasswordmanager.userservice.domain.services;

import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.repositories.AccountRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class JpaAccountServiceConfig {

    @Primary
    @Bean
    AccountService jpaUserService(@Autowired AccountRepository accountRepository) {
        return new JpaAccountServiceImpl(accountRepository);
    }
}
