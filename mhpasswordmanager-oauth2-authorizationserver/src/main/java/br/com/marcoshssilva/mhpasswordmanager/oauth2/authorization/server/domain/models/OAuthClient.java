package br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.domain.models;

import br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.domain.constants.OAuthAuthorizationGrantTypes;
import br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.domain.constants.OAuthClientAuthenticationMethods;

import lombok.*;

import java.util.LinkedList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString
public class OAuthClient {
    private String clientId;
    private String clientName;
    private String clientSecret;
    private OAuthClientAuthenticationMethods authenticationMethod = OAuthClientAuthenticationMethods.CLIENT_SECRET_BASIC;
    private List<OAuthAuthorizationGrantTypes> authenticationGrantTypes = new LinkedList<>();
    private List<String> redirectUris = new LinkedList<>();
    private List<String> scopes = new LinkedList<>();
    private OAuthTokenSettings tokenSettings = new OAuthTokenSettings();
    private OAuthClientSettings clientSettings = new OAuthClientSettings();
}
