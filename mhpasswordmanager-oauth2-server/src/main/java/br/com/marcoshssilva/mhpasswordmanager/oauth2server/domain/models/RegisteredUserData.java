package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models;

import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisteredUserData implements Serializable {
    public static final long serialVersionUID = 1L;

    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private Boolean isEnabled;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegisteredUserData that = (RegisteredUserData) o;
        return Objects.equals(username, that.username) && Objects.equals(email, that.email) && Objects.equals(firstName, that.firstName) && Objects.equals(lastName, that.lastName) && Objects.equals(isEnabled, that.isEnabled);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, email, firstName, lastName, isEnabled);
    }

    @Override
    public String toString() {
        return "RegisteredUserData{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", isEnabled=" + isEnabled +
                '}';
    }
}
