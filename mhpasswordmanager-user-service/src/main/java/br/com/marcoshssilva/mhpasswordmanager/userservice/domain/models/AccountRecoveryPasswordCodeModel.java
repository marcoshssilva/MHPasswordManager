package br.com.marcoshssilva.mhpasswordmanager.userservice.domain.models;

import java.time.LocalDateTime;

public record AccountRecoveryPasswordCodeModel(
        String code,
        String username,
        String ipClient,
        String userAgentClient,
        LocalDateTime createdAt,
        LocalDateTime expiresAt,
        Boolean completed
) {
}
