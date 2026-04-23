package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.config;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.JkwKeySelectorDispatcher;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.JwkKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwkKeySelectorDispatcherConfig {
    @Bean
    public JkwKeySelectorDispatcher jkwKeySelectorDispatcher(@Autowired JwkKeyService jwkKeyService) {
        return (JkwKeySelectorDispatcher) jwkKeyService;
    }
}
