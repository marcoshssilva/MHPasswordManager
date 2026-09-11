package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.clients.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountUserInternalResponseData implements Serializable {
    private String username;
    private Boolean enabled;
    @Builder.Default
    private Set<String> roles = Set.of();
    private String password;
}
