package br.com.marcoshssilva.mhpasswordmanager.oauth2server.configuration;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.RegisteredUserData;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.UserOperationsService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class JwtClaimsConfig {
    private static final List<OAuth2TokenType> ALLOWED_TOKEN_TYPES = List.of(OAuth2TokenType.ACCESS_TOKEN, OAuth2TokenType.REFRESH_TOKEN);
    private static final List<AuthorizationGrantType> ALLOWED_GRANT_TYPES = List.of(AuthorizationGrantType.AUTHORIZATION_CODE, AuthorizationGrantType.REFRESH_TOKEN);

    private final UserOperationsService userOperationsService;

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer() {
        return context -> {
            String username = null;
            RegisteredUserData user = null;

            if ((!ALLOWED_TOKEN_TYPES.contains(context.getTokenType()))
                 || !ALLOWED_GRANT_TYPES.contains(context.getAuthorizationGrantType())) {
                return;
            }

            username = context.getPrincipal().getName();
            if (Objects.isNull(username)) {
                return;
            }

            user = userOperationsService.getUserByUsername(username);
            if (Objects.isNull(user)) {
                return;
            }

            Set<String> authorities = context.getPrincipal()
                    .getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toSet());

            Set<String> roles = authorities.stream()
                    .filter(authority -> authority.startsWith("ROLE_"))
                    .map(authority -> authority.substring("ROLE_".length()))
                    .collect(Collectors.toSet());

            Set<String> groups = authorities.stream()
                    .filter(authority -> authority.startsWith("GROUP_"))
                    .map(authority -> authority.substring("GROUP_".length()))
                    .collect(Collectors.toSet());

            JwtClaimsSet.Builder claims = context.getClaims();

            claims.claim("username", user.getUsername());
            claims.claim("email", user.getEmail());
            claims.claim("first_name", user.getFirstName());
            claims.claim("last_name", user.getLastName());
            claims.claim("name", user.getFirstName() + " " + user.getLastName());
            claims.claim("roles", roles);
            claims.claim("groups", groups);
        };
    }
}
