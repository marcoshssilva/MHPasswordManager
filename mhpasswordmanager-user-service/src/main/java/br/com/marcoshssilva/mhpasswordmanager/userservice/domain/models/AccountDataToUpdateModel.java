package br.com.marcoshssilva.mhpasswordmanager.userservice.domain.models;

import java.util.Objects;

public record AccountDataToUpdateModel(String firstName, String lastName) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccountDataToUpdateModel that)) return false;
        return Objects.equals(firstName, that.firstName) && Objects.equals(lastName, that.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName);
    }
}
