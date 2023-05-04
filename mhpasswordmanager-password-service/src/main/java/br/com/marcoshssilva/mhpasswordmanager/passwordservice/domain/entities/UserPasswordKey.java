package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.enums.PasswordKeyTypesEnum;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Getter
@Setter
@Table(name = "users_keys")
@Builder
public class UserPasswordKey implements Serializable {
    public static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_registration_id", nullable = false)
    private UserRegistration userRegistration;

    @ElementCollection
    @CollectionTable(name = "users_key_tags")
    @Column(nullable = false)
    private Collection<String> tags = Collections.emptySet();

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false, name = "user_key_type_id")
    private PasswordKeyTypesEnum type;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "last_update")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserPasswordKey that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(userRegistration, that.userRegistration) && Objects.equals(tags, that.tags) && Objects.equals(description, that.description) && type == that.type && Objects.equals(createdAt, that.createdAt) && Objects.equals(lastUpdate, that.lastUpdate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userRegistration, tags, description, type, createdAt, lastUpdate);
    }
}
