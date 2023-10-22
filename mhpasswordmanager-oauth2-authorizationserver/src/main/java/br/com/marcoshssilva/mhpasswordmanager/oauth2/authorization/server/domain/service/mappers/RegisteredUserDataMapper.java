package br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.domain.service.mappers;

import br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.domain.models.RegisteredUserData;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RegisteredUserDataMapper implements RowMapper<RegisteredUserData> {
    @Override
    public RegisteredUserData mapRow(ResultSet rs, int rowNum) throws SQLException {
        return RegisteredUserData.builder()
                .username(rs.getString("username"))
                .email(rs.getString("email"))
                .firstName(rs.getString("firstname"))
                .lastName(rs.getString("lastname"))
                .isEnabled(rs.getBoolean("enabled"))
                .build();
    }
}
