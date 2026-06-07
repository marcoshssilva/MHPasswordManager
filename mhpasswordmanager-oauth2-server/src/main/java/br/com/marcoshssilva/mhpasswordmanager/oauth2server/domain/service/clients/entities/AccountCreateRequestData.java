package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.clients.entities;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountCreateRequestData implements Serializable {
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String password;
    private Set<String> roles;

    public static AccountCreateRequestData from(UserDetails user) {
        return AccountCreateRequestData.builder()
                .firstName(user.getUsername())
                .lastName(user.getUsername())
                .email(user.getUsername())
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .map(AccountCreateRequestData::toDefaultUserRole)
                        .collect(Collectors.toSet()))
                .build();
    }

    private static String toDefaultUserRole(String authority) {
        if (authority != null && authority.startsWith("ROLE_")) {
            return authority.substring("ROLE_".length());
        }
        return authority;
    }
}
