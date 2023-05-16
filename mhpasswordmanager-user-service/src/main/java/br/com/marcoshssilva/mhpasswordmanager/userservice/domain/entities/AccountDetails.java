package br.com.marcoshssilva.mhpasswordmanager.userservice.domain.entities;

import lombok.*;

import javax.persistence.*;

import java.io.Serializable;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "users_details")
@Entity
@Getter
@Setter
@Builder
public class AccountDetails implements Serializable {
    public static final long serialVersionUID = 1L;

    @EmbeddedId
    private AccountDetailsPK id;

    @Column(name = "firstname", length = 32, nullable = false)
    private String firstName;

    @Column(name = "lastname", length = 32, nullable = false)
    private String lastName;

    @Column(name = "imageurl")
    private String imageUrl;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccountDetails that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(firstName, that.firstName) && Objects.equals(lastName, that.lastName) && Objects.equals(imageUrl, that.imageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, imageUrl);
    }
}
