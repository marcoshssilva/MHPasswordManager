package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@lombok.ToString
@lombok.Getter
@lombok.Setter
@lombok.Builder

@Entity
@Table(name = "users_buckets")
public class UserBucket implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(nullable = false, length = 36)
    private String id;

    @Column(nullable = false, length = 40)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String encodedPublicKey;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String encryptedPrivateKeyWithPassword;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP")
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_update", nullable = false, columnDefinition = "TIMESTAMP")
    private Date lastUpdate;

    @Column(name = "owner_name", nullable = false)
    private String ownerName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserBucket that = (UserBucket) o;
        return Objects.equals(id, that.id)
                && Objects.equals(name, that.name)
                && Objects.equals(description, that.description)
                && Objects.equals(encodedPublicKey, that.encodedPublicKey)
                && Objects.equals(encryptedPrivateKeyWithPassword, that.encryptedPrivateKeyWithPassword)
                && Objects.equals(createdAt, that.createdAt)
                && Objects.equals(lastUpdate, that.lastUpdate)
                && Objects.equals(ownerName, that.ownerName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, encodedPublicKey, encryptedPrivateKeyWithPassword, createdAt, lastUpdate, ownerName);
    }
}
