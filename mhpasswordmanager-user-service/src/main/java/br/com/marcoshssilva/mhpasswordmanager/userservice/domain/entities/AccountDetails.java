package br.com.marcoshssilva.mhpasswordmanager.userservice.domain.entities;

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
@Table(name = "users_details")
@Entity
public class AccountDetails implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "username", length = 50, nullable = false)
    @Getter
    @Setter
    private String username;

    @Getter
    @Setter
    @Column(name = "firstname", length = 32, nullable = false)
    private String firstName;

    @Getter
    @Setter
    @Column(name = "lastname", length = 32, nullable = false)
    private String lastName;

    @Getter
    @Setter
    @Column(name = "imageurl")
    private String imageUrl;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountDetails that = (AccountDetails) o;
        return username.equals(that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}
