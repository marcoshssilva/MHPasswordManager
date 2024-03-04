package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.models;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.UserAuthorizations;

import java.util.Objects;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserAuthorizationModel that = (UserAuthorizationModel) o;
        return Objects.equals(profiles, that.profiles) && Objects.equals(roles, that.roles) && Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(profiles, roles, username);
    }
}
