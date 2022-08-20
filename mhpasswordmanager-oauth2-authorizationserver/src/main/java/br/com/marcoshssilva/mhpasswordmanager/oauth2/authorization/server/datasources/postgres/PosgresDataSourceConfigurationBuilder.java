package br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.datasources.postgres;

import br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.datasources.JdbcDataSourceConfigurationBuilder;
import org.springframework.boot.jdbc.DataSourceBuilder;

import javax.sql.DataSource;

public class PosgresDataSourceConfigurationBuilder implements JdbcDataSourceConfigurationBuilder {

    @Override
    public DataSource getInstance() {
        return DataSourceBuilder.create().driverClassName("org.postgresql.Driver")
                .password("postgres")
                .username("postgres")
                .url("jdbc:postgresql://localhost:5432/db_user_service")
                .build();

    }
}
