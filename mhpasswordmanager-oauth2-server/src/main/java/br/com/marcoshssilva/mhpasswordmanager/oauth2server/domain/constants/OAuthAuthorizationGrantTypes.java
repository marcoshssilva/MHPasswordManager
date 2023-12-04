package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

import org.springframework.security.oauth2.core.AuthorizationGrantType;

@AllArgsConstructor
public enum OAuthAuthorizationGrantTypes {
    AUTHORIZATION_CODE(AuthorizationGrantType.AUTHORIZATION_CODE),
    REFRESH_TOKEN(AuthorizationGrantType.REFRESH_TOKEN),
    CLIENT_CREDENTIALS(AuthorizationGrantType.CLIENT_CREDENTIALS),
    PASSWORD(AuthorizationGrantType.PASSWORD),
    JWT_BEARER(AuthorizationGrantType.JWT_BEARER);
    @Getter
    private final AuthorizationGrantType authorizationGrantType;
}
