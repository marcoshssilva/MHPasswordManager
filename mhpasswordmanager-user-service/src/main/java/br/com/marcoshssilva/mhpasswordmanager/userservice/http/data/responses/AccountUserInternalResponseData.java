package br.com.marcoshssilva.mhpasswordmanager.userservice.http.data.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@ToString
@Builder
public class AccountUserInternalResponseData implements Serializable {
    private String username;
    private Boolean enabled;
    private Set<String> roles;
    private String password;

    public static AccountUserInternalResponseData fromUser(UserDetails user) {
        return AccountUserInternalResponseData.builder()
                .username(user.getUsername())
                .enabled(user.isEnabled())
                .roles(user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet()))
                .password(user.getPassword())
                .build();
    }
}
