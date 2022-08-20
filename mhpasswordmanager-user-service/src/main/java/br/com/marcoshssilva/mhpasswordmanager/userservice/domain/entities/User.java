package br.com.marcoshssilva.mhpasswordmanager.userservice.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "Accounts")
@Entity(name = "User")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id", nullable = false)
    Long userId;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    String username;

    @Column(name = "password", nullable = false, length = 72)
    String password;

    @Column(name = "email", nullable = false, unique = true)
    String email;

    @Column(name = "created_on", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    Date createOn;

    @Column(name = "last_login", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    Date lastLogin;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}
