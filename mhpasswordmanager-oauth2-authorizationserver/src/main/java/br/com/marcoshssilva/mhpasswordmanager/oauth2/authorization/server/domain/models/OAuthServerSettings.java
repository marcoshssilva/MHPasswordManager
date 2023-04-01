package br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.domain.models;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString
public class OAuthServerSettings {
    private String authorizeEndpoint = "/oauth2/authorize";
    private String tokenEndpoint = "/oauth2/token";
    private String tokenJwkSetEndpoint = "/oauth2/jwks";
    private String tokenRevocationEndpoint = "/oauth2/revoke";
    private String tokenIntrospectEndpoint = "/oauth2/introspect";
    private String oidcClientRegistrationEndpoint = "/connect/register";
    private String oidcUserInfoEndpoint = "/userinfo";
}