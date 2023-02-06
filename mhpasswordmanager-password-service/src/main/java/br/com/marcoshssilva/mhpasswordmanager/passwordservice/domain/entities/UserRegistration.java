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

    @Column(columnDefinition = "TEXT")
    @Getter
    @Setter
    private String encriptedKey10;

    @Column(columnDefinition = "TEXT")
    @Getter
    @Setter
    private String encriptedKey1;

    @Column(columnDefinition = "TEXT")
    @Getter
    @Setter
    private String encriptedKey2;

    @Column(columnDefinition = "TEXT")
    @Getter
    @Setter
    private String encriptedKey3;

    @Column(columnDefinition = "TEXT")
    @Getter
    @Setter
    private String encriptedKey4;

    @Column(columnDefinition = "TEXT")
    @Getter
    @Setter
    private String encriptedKey5;

    @Column(columnDefinition = "TEXT")
    @Getter
    @Setter
    private String encriptedKey6;

    @Column(columnDefinition = "TEXT")
    @Getter
    @Setter
    private String encriptedKey7;

    @Column(columnDefinition = "TEXT")
    @Getter
    @Setter
    private String encriptedKey8;

    @Column(columnDefinition = "TEXT")
    @Getter
    @Setter
    private String encriptedKey9;

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
