package br.com.marcoshssilva.mhpasswordmanager.emailservice.configs.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CustomJwtGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    private static final String AUTHORITIES_CLAIM_NAME = "authorities";

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        List<String> userRoleAuthorities = jwt.getClaimAsStringList(AUTHORITIES_CLAIM_NAME);
        if (userRoleAuthorities == null) {
            userRoleAuthorities = Collections.emptyList();
        }

        JwtGrantedAuthoritiesConverter scopesConverter = new JwtGrantedAuthoritiesConverter();
        Collection<GrantedAuthority> scopeAuthorities = scopesConverter.convert(jwt);
        scopeAuthorities.addAll(userRoleAuthorities.stream()
                .map(SimpleGrantedAuthority::new)
                .toList());

        return scopeAuthorities;
    }
}
