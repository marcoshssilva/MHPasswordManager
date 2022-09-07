package br.com.marcoshssilva.mhpasswordmanager.userservice.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(schema = "db_users", name = "users")
@Entity(name = "User")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "username", length = 50, nullable = false)
    private String username;

    @Column(name = "password", length = 120, nullable = false)
    private String password;

    @Column(name = "enabled")
    private Boolean enabled;

    @Column(name = "authority")
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(schema = "db_users", name = "authorities",joinColumns = @JoinColumn(name = "username", referencedColumnName = "username"))
    private Set<String> roles;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return username.equals(user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}
