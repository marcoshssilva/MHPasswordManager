package br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.datasources;

import javax.sql.DataSource;

public interface JdbcDataSourceConfigurationBuilder {
    DataSource getInstance();
}
