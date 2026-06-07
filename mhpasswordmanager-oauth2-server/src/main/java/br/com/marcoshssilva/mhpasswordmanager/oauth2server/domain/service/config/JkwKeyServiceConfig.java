package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.config;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.JwkKeyService;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.impl.ClassPathJwkKeyServiceImpl;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.impl.JdbcJkwKeyServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class JkwKeyServiceConfig {
    private static final Logger LOG = LoggerFactory.getLogger(JkwKeyServiceConfig.class);

    @Bean
    @ConditionalOnProperty(name = "config.jwk.mode", havingValue = "DATABASE")
    @Primary
    public JwkKeyService jdbcJwkKeyService(@Autowired JdbcTemplate jdbcTemplate) {
        LOG.info("JwkKeyService using JdbcJwkKeyService with auth-db datasource");
        return new JdbcJkwKeyServiceImpl(jdbcTemplate);
    }

    @Bean
    @ConditionalOnProperty(name = "config.jwk.mode", havingValue = "CLASSPATH")
    public JwkKeyService classPathJwkKeyService() {
        LOG.info("JwkKeyService using ClassPathJwkKeyService with default key");
        return new ClassPathJwkKeyServiceImpl();
    }
}
