package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(schema = "db_passwords", name = "users_registration")
public class UserRegistration implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Getter
    @Setter
    private String id;

    @Column(nullable = false)
    @Getter
    @Setter
    private String email;

    @Column(columnDefinition = "TEXT")
    @Getter
    @Setter
    private String publicKey;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserRegistration)) return false;
        UserRegistration that = (UserRegistration) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
