package br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.datasources.h2;

import br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.datasources.JdbcDataSourceConfigurationBuilder;

import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

public class H2DataSourceConfigurationBuilder implements JdbcDataSourceConfigurationBuilder {

    @Override
    public DataSource getInstance() {
        return new EmbeddedDatabaseBuilder().setName("AUTHORIZATION-SERVER-DB")
                .setType(EmbeddedDatabaseType.H2)
                .setScriptEncoding("UTF-8")
                .addScript("org/springframework/security/oauth2/server/authorization/oauth2-authorization-schema.sql")
                .addScript("org/springframework/security/oauth2/server/authorization/oauth2-authorization-consent-schema.sql")
                .addScript("org/springframework/security/oauth2/server/authorization/client/oauth2-registered-client-schema.sql")
                .build();
    }
}
