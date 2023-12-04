package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

@AllArgsConstructor
public enum OAuthClientAuthenticationMethods {
    NONE(ClientAuthenticationMethod.NONE),
    PRIVATE_KEY_JWT(ClientAuthenticationMethod.PRIVATE_KEY_JWT),
    CLIENT_SECRET_JWT(ClientAuthenticationMethod.CLIENT_SECRET_JWT),
    CLIENT_SECRET_BASIC(ClientAuthenticationMethod.CLIENT_SECRET_BASIC),
    CLIENT_SECRET_POST(ClientAuthenticationMethod.CLIENT_SECRET_POST);
    @Getter
    private final ClientAuthenticationMethod clientAuthenticationMethod;
}
