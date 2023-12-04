package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString
public class OAuthClientSettings {
    private Boolean requireAuthorizationConsent = false;
    private Boolean requireProofKey = false;
}
