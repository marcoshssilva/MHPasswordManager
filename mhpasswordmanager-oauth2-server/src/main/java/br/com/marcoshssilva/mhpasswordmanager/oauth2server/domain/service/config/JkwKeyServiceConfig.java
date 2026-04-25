package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.config;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.JwkKeyService;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.impl.ClassPathJwkKeyServiceImpl;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.impl.JdbcJkwKeyServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@RequiredArgsConstructor
public class JkwKeyServiceConfig {
    @Qualifier("jdbcJwkKeyService")
    @Bean
    @Profile("!test & !embedded-database & !in-memory-client")
    public JwkKeyService jdbcJwkKeyService(@Autowired @Qualifier("dbAuthJdbcTemplate") JdbcTemplate jdbcTemplate) {
        return new JdbcJkwKeyServiceImpl(jdbcTemplate);
    }

    @Qualifier("jdbcJwkKeyService")
    @Bean
    @Profile("embedded-database")
    public JwkKeyService embeddedJwkKeyService(@Autowired @Qualifier("embeddedJdbcTemplate") JdbcTemplate jdbcTemplate) {
        return new JdbcJkwKeyServiceImpl(jdbcTemplate);
    }

    @Qualifier("classPathJwkKeyService")
    @Bean
    @Profile({"test", "in-memory-client"})
    @Primary
    public JwkKeyService classPathJwkKeyService() {
        return new ClassPathJwkKeyServiceImpl();
    }
}
