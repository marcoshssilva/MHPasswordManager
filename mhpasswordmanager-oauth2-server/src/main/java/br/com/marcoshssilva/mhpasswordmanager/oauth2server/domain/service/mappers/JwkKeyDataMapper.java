package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.mappers;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.JwkKeyData;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;

public class JwkKeyDataMapper implements RowMapper<JwkKeyData> {
    private static JwkKeyDataMapper singleton = null;

    public static JwkKeyDataMapper getInstance() {
        if (singleton == null) {
            singleton = new JwkKeyDataMapper();
        }
        return singleton;
    }

    @Nullable
    @Override
    public JwkKeyData mapRow(ResultSet rs, int rowNum) throws SQLException {
        return JwkKeyData.builder()
                .privateKey(rs.getString("base64_private_key"))
                .publicKey(rs.getString("base64_public_key"))
                .uuid(rs.getString("uuid"))
                .active(rs.getBoolean("active"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .deletedAt(rs.getTimestamp("deleted_at") != null ? rs.getTimestamp("deleted_at").toLocalDateTime() : null)
                .algorithm(rs.getString("algorithm"))
                .build();
    }
}
