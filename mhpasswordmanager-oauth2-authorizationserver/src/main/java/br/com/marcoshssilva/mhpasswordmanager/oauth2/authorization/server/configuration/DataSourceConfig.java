package br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.configuration;

import br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.datasources.JdbcDataSourceConfigurationBuilder;
import br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.datasources.h2.H2DataSourceConfigurationBuilder;
import br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.datasources.postgres.PosgresDataSourceConfigurationBuilder;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Slf4j
@Configuration
public class DataSourceConfig {

    @Bean()
    @Primary
    public JdbcDataSourceConfigurationBuilder jdbcDataSourceConfigurationBuilder() {
        return new PosgresDataSourceConfigurationBuilder();
    }

    @Bean("InMemory")
    public JdbcDataSourceConfigurationBuilder jdbcInMemoryDataSourceConfigurationBuilder() {
        return new H2DataSourceConfigurationBuilder();
    }

    @Bean
    @Primary
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    @Primary
    public DataSource dataSource(JdbcDataSourceConfigurationBuilder jdbcDataSourceConfigurationBuilder) {
        return jdbcDataSourceConfigurationBuilder.getInstance();
    }

}
