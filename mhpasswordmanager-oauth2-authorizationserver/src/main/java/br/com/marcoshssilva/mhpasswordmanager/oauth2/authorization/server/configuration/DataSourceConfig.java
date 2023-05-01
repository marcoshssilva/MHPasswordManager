package br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    private static final Logger LOG = LoggerFactory.getLogger(DataSourceConfig.class);

    @Bean
    @ConfigurationProperties("spring.datasource.users")
    public DataSourceProperties dbUsersDatasourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean
    @ConfigurationProperties("spring.datasource.auth")
    public DataSourceProperties dbAuthDatasourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Profile("!test & !embedded-database & !in-memory-client")
    public JdbcTemplate dbAuthJdbcTemplate(@Qualifier("dataSourceDbAuth") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    @Profile("embedded-database")
    public JdbcTemplate embeddedJdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    @Profile("!embedded-database & !in-memory-users")
    public JdbcTemplate dbUsersJdbcTemplate(@Qualifier("dataSourceDbUsers") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    @Profile("!embedded-database & !in-memory-client")
    public DataSource dataSourceDbAuth(@Qualifier("dbAuthDatasourceProperties") DataSourceProperties dataSourceProperties) {
        LOG.info("Starting database using DB-Auth datasource");
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }

    @Bean
    @Profile("!embedded-database & !in-memory-users")
    public DataSource dataSourceDbUsers(@Qualifier("dbUsersDatasourceProperties") DataSourceProperties dataSourceProperties) {
        LOG.info("Starting database using DB-Users datasource");
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }

    @Bean
    @Profile("embedded-database")
    public DataSource embeddedDatabase() {
        LOG.info("Starting database using Embedded-Database");
        return new EmbeddedDatabaseBuilder().generateUniqueName(true)
                .setType(EmbeddedDatabaseType.H2)
                .setScriptEncoding("UTF-8")
                .addScript("org/springframework/security/oauth2/server/authorization/oauth2-authorization-schema.sql")
                .addScript("org/springframework/security/oauth2/server/authorization/oauth2-authorization-consent-schema.sql")
                .addScript("org/springframework/security/oauth2/server/authorization/client/oauth2-registered-client-schema.sql")
                .addScript("db/h2/schema.sql")
                .addScript("db/h2/data.sql")
                .build();
    }


}
