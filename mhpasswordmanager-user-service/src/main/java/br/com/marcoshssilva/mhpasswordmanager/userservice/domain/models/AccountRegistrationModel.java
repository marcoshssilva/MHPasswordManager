package br.com.marcoshssilva.mhpasswordmanager.userservice.domain.models;

import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.enums.DefaultUserRoles;

import java.util.Objects;
import java.util.Set;

public record AccountRegistrationModel(String email, String username, String password, Boolean enabled, Set<DefaultUserRoles> roles, String firstName, String lastName) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccountRegistrationModel that)) return false;
        return Objects.equals(email, that.email) && Objects.equals(username, that.username) && Objects.equals(password, that.password) && Objects.equals(enabled, that.enabled) && Objects.equals(roles, that.roles) && Objects.equals(firstName, that.firstName) && Objects.equals(lastName, that.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, username, password, enabled, roles, firstName, lastName);
    }
}
