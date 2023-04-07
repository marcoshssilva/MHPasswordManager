package br.com.marcoshssilva.mhpasswordmanager.userservice.domain.services.config;

import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.repositories.AccountDetailsRepository;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.repositories.AccountRepository;

import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.services.AccountService;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.services.impl.JpaAccountServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AccountServiceConfig {
    @Primary
    @Bean
    AccountService jpaUserService(@Autowired AccountRepository accountRepository, @Autowired AccountDetailsRepository accountDetailsRepository, @Autowired PasswordEncoder encoder) {
        return new JpaAccountServiceImpl(accountRepository, accountDetailsRepository, encoder);
    }
}
