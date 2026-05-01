package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models;

import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString
public class RecoveryPasswordCodeRequest {
    String code;
    String username;
    String ipClient;
    String userAgentClient;
    LocalDate createdAt;
    LocalDate expiresAt;
    Boolean completed;
}
