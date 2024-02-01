package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.models;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.UserAuthorizations;

import java.util.Set;

@lombok.AllArgsConstructor
@lombok.Builder
public class UserAuthorizationModel implements UserAuthorizations {
    private Set<String> profiles;
    private Set<String> roles;
    private String username;

    @Override
    public Set<String> getProfiles() {
        return this.profiles;
    }

    @Override
    public Set<String> getRoles() {
        return this.roles;
    }

    @Override
    public String getUsername() {
        return this.username;
    }
}
