package br.com.marcoshssilva.mhpasswordmanager.userservice.domain.entities;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@RequiredArgsConstructor
@ToString
@Table(schema = "db_users", name = "users")
@Entity
public class Account implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "username", length = 50, nullable = false)
    @Getter
    @Setter
    private String username;

    @Column(name = "password", length = 120, nullable = false)
    @Getter
    @Setter
    private String password;

    @Column(name = "enabled")
    @Getter
    @Setter
    private Boolean enabled;

    @Column(name = "authority")
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(schema = "db_users", name = "authorities",joinColumns = @JoinColumn(name = "username", referencedColumnName = "username"))
    @Getter
    @Setter
    private Set<String> roles = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return username.equals(account.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}
