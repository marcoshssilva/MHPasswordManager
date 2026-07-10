package br.com.marcoshssilva.mhpasswordmanager.oauth2server.configuration;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.RegisteredUserData;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.UserOperationsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Configuration
public class JwtClaimsConfig {
    private final UserOperationsService userOperationsService;

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer() {
        return context -> {
            if (!OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
                return;
            }

            String username = context.getPrincipal().getName();

            RegisteredUserData user = userOperationsService.getUserByUsername(username);

            if (Objects.isNull(user)) {
                return;
            }

            List<String> authorities = context.getPrincipal()
                    .getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            List<String> roles = authorities.stream()
                    .filter(authority -> authority.startsWith("ROLE_"))
                    .map(authority -> authority.substring("ROLE_".length()))
                    .toList();

            List<String> groups = authorities.stream()
                    .filter(authority -> authority.startsWith("GROUP_"))
                    .map(authority -> authority.substring("GROUP_".length()))
                    .toList();

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
