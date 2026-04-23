package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString
public class JwkKeyData {
    private String privateKey;
    private String publicKey;
    private String uuid;
    private String algorithm;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;
    private Boolean active;
}
