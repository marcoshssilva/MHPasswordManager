package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.mappers;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.RecoveryPasswordCodeRequest;
import org.springframework.jdbc.core.RowMapper;

public class RecoveryPasswordCodeRequestMapper implements RowMapper<RecoveryPasswordCodeRequest> {
    private static RecoveryPasswordCodeRequestMapper singleton = null;
    public static RecoveryPasswordCodeRequestMapper getInstance() {
        if (singleton == null) {
            singleton = new RecoveryPasswordCodeRequestMapper();
        }
        return singleton;
    }

    @Override
    public RecoveryPasswordCodeRequest mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        return RecoveryPasswordCodeRequest.builder()
                .code(rs.getString("code"))
                .username(rs.getString("username"))
                .ipClient(rs.getString("ip_client"))
                .userAgentClient(rs.getString("user_agent_client"))
                .createdAt(rs.getDate("created_at").toLocalDate())
                .expiresAt(rs.getDate("expires_at").toLocalDate())
                .completed(rs.getBoolean("completed"))
                .build();
    }
}
