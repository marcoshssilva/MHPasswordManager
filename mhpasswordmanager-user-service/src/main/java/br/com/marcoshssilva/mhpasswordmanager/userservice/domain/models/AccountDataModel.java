package br.com.marcoshssilva.mhpasswordmanager.userservice.domain.models;

import java.util.Objects;
import java.util.Set;

public record AccountDataModel(String username, String password, Boolean enabled, Set<String> roles, String firstName, String lastName, String imageUrl) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccountDataModel that)) return false;
        return Objects.equals(username, that.username) && Objects.equals(password, that.password) && Objects.equals(enabled, that.enabled) && Objects.equals(roles, that.roles) && Objects.equals(firstName, that.firstName) && Objects.equals(lastName, that.lastName) && Objects.equals(imageUrl, that.imageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password, enabled, roles, firstName, lastName, imageUrl);
    }
}
