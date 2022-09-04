package br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Value("${SPRING_DATASOURCE_URL:jdbc:postgresql://localhost:5432/postgres?currentSchema=db_users}")
    private String datasourceUrl;

    @Value("${SPRING_DATASOURCE_USERNAME:postgres}")
    private String datasourceUsername;

    @Value("${SPRING_DATASOURCE_PASSWORD:postgres}")
    private String datasourcePassword;

    @Value("${SPRING_DATASOURCE_DRIVER:org.postgresql.Driver}")
    private String datasourceDriver;

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public DataSource dataSource() {
        return DataSourceBuilder.create().driverClassName(datasourceDriver)
                .url(datasourceUrl)
                .username(datasourceUsername)
                .password(datasourcePassword)
                .build();
    }

}
