package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models;

import lombok.*;

import java.util.Collection;
import java.util.Collections;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString
public class OAuthUser {
    private String username;
    private String password;
    private Collection<String> roles = Collections.emptyList();
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;
}
