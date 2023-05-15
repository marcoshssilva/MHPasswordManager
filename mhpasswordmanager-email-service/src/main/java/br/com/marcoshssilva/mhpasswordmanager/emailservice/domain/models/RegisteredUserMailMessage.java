package br.com.marcoshssilva.mhpasswordmanager.emailservice.domain.models;

import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisteredUserMailMessage implements Serializable {
    public static final long serialVersionUID = 1L;

    private String name;
    private String email;
    private String link;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RegisteredUserMailMessage that)) return false;
        return Objects.equals(name, that.name) && Objects.equals(email, that.email) && Objects.equals(link, that.link);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, email);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RegisteredUserMailMessage{");
        sb.append("name='").append(name).append('\'');
        sb.append(", email='").append(email).append('\'');
        sb.append(", link='").append(link).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
