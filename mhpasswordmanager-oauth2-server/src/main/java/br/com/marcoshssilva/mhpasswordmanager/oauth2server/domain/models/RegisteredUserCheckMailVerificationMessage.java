package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models;

import lombok.*;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisteredUserCheckMailVerificationMessage {
    private String link;
    private String name;
    private String email;

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof RegisteredUserCheckMailVerificationMessage that)) return false;
        return Objects.equals(link, that.link) && Objects.equals(name, that.name) && Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(link);
        result = 31 * result + Objects.hashCode(name);
        result = 31 * result + Objects.hashCode(email);
        return result;
    }

    @Override
    public String toString() {
        return "RegisteredUserCheckMailVerificationMessage{" +
                "link='" + link + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
