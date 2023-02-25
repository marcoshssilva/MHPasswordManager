package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "users_stored_values")
@Builder
public class UserPasswordStoredValue implements Serializable {
    public static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_key_id", nullable = false)
    @Getter
    @Setter
    private UserPasswordKey keyId;

    @Column(nullable = false, columnDefinition = "TEXT")
    @Getter
    @Setter
    private String data;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    @Getter
    @Setter
    private Date createdAt;

    @Column(name = "last_update")
    @Temporal(TemporalType.TIMESTAMP)
    @Getter
    @Setter
    private Date lastUpdate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserPasswordStoredValue that)) return false;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
