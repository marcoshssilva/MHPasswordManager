package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities;

import lombok.*;

import javax.persistence.*;
import java.io.Serial;
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
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_bucket_id", nullable = false)
    private UserBucket userBucket;

    @ElementCollection
    @CollectionTable(name = "users_key_tags")
    @Column(nullable = false)
    private Collection<String> tags = Collections.emptySet();

    @Column(nullable = false)
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_key_type_id", nullable = false, referencedColumnName = "id")
    private UserPasswordKeyType type;

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
        return Objects.equals(id, that.id) && Objects.equals(userBucket, that.userBucket) && Objects.equals(tags, that.tags) && Objects.equals(description, that.description) && Objects.equals(type, that.type) && Objects.equals(createdAt, that.createdAt) && Objects.equals(lastUpdate, that.lastUpdate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userBucket, tags, description, type, createdAt, lastUpdate);
    }
}
