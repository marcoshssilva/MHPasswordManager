package br.com.marcoshssilva.mhpasswordmanager.userservice.domain.entities;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "users")
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
    @CollectionTable(name = "authorities",joinColumns = @JoinColumn(name = "username", referencedColumnName = "username"))
    @Getter
    @Setter
    private Set<String> roles = new HashSet<>();

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "username")
    @Getter
    @Setter
    private AccountDetails accountDetails;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account account)) return false;
        return Objects.equals(username, account.username) && Objects.equals(password, account.password) && Objects.equals(enabled, account.enabled) && Objects.equals(roles, account.roles) && Objects.equals(accountDetails, account.accountDetails);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password, enabled, roles, accountDetails);
    }
}
