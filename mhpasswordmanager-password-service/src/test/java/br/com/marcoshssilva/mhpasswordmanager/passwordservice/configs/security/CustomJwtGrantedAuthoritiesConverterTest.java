package br.com.marcoshssilva.mhpasswordmanager.passwordservice.configs.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomJwtGrantedAuthoritiesConverterTest {
    @DisplayName("Should test if authorities has included in CustomJwtGrantedAuthorities")
    @Test
    void testConvert() {
        CustomJwtGrantedAuthoritiesConverter converter = new CustomJwtGrantedAuthoritiesConverter();

        // Create a mock JWT object with a sample "authorities" claim
        List<String> userRoleAuthorities = List.of("SCOPE_read", "SCOPE_write", "ROLE_USER", "ROLE_ADMIN");
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsStringList("authorities")).thenReturn(userRoleAuthorities);
        when(jwt.getIssuedAt()).thenReturn(Instant.now());
        when(jwt.getExpiresAt()).thenReturn(Instant.now().plusSeconds(3600));

        // Convert the JWT to a collection of GrantedAuthority objects
        Collection<GrantedAuthority> authorities = converter.convert(jwt);

        // Assert that the expected authorities were generated
        List<GrantedAuthority> expectedAuthorities = List.of(new SimpleGrantedAuthority("SCOPE_read"), new SimpleGrantedAuthority("SCOPE_write"), new SimpleGrantedAuthority("ROLE_USER"), new SimpleGrantedAuthority("ROLE_ADMIN"));
        assertEquals(expectedAuthorities, authorities);
    }
}
