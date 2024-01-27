package br.com.marcoshssilva.mhpasswordmanager.userservice.configs.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Slf4j
public class CustomJwtGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    private static final String AUTHORITIES_CLAIM_NAME = "authorities";

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        List<String> userRoleAuthorities = null;
        JwtGrantedAuthoritiesConverter scopesConverter;
        Collection<GrantedAuthority> scopeAuthorities = null;

        try {
            userRoleAuthorities = jwt.getClaimAsStringList(AUTHORITIES_CLAIM_NAME);
        } catch (Exception e) {
            log.error("Cannot get AUTHORITIES FROM Token.", e);
        } finally {
            if (userRoleAuthorities == null) {
                userRoleAuthorities = Collections.emptyList();
            }
        }

        try {
            scopesConverter = new JwtGrantedAuthoritiesConverter();
            scopeAuthorities = scopesConverter.convert(jwt);
        } catch (Exception e) {
            log.error("Cannot parse JWT Token.", e);
        } finally {
            if (scopeAuthorities == null) {
                scopeAuthorities = Collections.emptyList();
            }
        }

        scopeAuthorities.addAll(userRoleAuthorities.stream()
                .map(SimpleGrantedAuthority::new)
                .toList());

        return scopeAuthorities;
    }
}
