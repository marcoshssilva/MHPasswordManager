package br.com.marcoshssilva.mhpasswordmanager.oauth2server.configuration;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.RegisteredUserData;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.UserOperationsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtClaimsConfigTests {

    @Mock
    private UserOperationsService userOperationsService;

    private JwtClaimsConfig jwtClaimsConfig;

    @BeforeEach
    void setUp() {
        jwtClaimsConfig = new JwtClaimsConfig(userOperationsService);
    }

    @DisplayName("Should customize claims when token is access token and grant type is authorization code")
    @Test
    void shouldCustomizeClaims() {
        OAuth2TokenCustomizer<JwtEncodingContext> customizer = jwtClaimsConfig.jwtCustomizer();

        JwsHeader.Builder jwsHeader = JwsHeader.with(SignatureAlgorithm.RS256);
        JwtClaimsSet.Builder claimsBuilder = JwtClaimsSet.builder();

        UsernamePasswordAuthenticationToken principal = new UsernamePasswordAuthenticationToken(
                "testuser",
                "credentials",
                List.of(new SimpleGrantedAuthority("ROLE_USER"), new SimpleGrantedAuthority("GROUP_ADMINS"))
        );

        RegisteredUserData user = RegisteredUserData.builder()
                .username("testuser")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .build();

        when(userOperationsService.getUserByUsername("testuser")).thenReturn(user);

        JwtEncodingContext context = JwtEncodingContext.with(jwsHeader, claimsBuilder)
                .tokenType(OAuth2TokenType.ACCESS_TOKEN)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .principal(principal)
                .build();

        customizer.customize(context);

        JwtClaimsSet claims = context.getClaims().build();
        assertEquals("testuser", claims.getClaim("username"));
        assertEquals("test@example.com", claims.getClaim("email"));
        assertEquals("Test", claims.getClaim("first_name"));
        assertEquals("User", claims.getClaim("last_name"));
        assertEquals("Test User", claims.getClaim("name"));
        assertEquals(Set.of("USER"), claims.getClaim("roles"));
        assertEquals(Set.of("ADMINS"), claims.getClaim("groups"));
    }

    @DisplayName("Should ignore customization when grant type is not allowed")
    @Test
    void shouldIgnoreWhenGrantTypeNotAllowed() {
        OAuth2TokenCustomizer<JwtEncodingContext> customizer = jwtClaimsConfig.jwtCustomizer();

        JwsHeader.Builder jwsHeader = JwsHeader.with(SignatureAlgorithm.RS256);
        JwtClaimsSet.Builder claimsBuilder = JwtClaimsSet.builder();

        JwtEncodingContext context = JwtEncodingContext.with(jwsHeader, claimsBuilder)
                .tokenType(OAuth2TokenType.ACCESS_TOKEN)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .build();

        customizer.customize(context);

        verifyNoInteractions(userOperationsService);
    }
}
