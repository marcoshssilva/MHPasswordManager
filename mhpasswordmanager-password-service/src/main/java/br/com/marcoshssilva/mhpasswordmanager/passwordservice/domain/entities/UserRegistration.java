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
@Table(name = "users_registration")
@Builder
@Getter
@Setter
public class UserRegistration implements Serializable {
    public static final long serialVersionUID = 1L;

    @Id
    @Column(nullable = false, length = 36)
    private String id;

    @Column(nullable = false)
    private String email;

    @Column(columnDefinition = "TEXT")
    private String encodedPublicKey;

    @Column(columnDefinition = "TEXT")
    private String encryptedPrivateKeyWithPassword;

    @Column(columnDefinition = "TEXT")
    private String encryptedPrivateKey0;

    @Column(columnDefinition = "TEXT")
    private String encryptedPrivateKey1;

    @Column(columnDefinition = "TEXT")
    private String encryptedPrivateKey2;

    @Column(columnDefinition = "TEXT")
    private String encryptedPrivateKey3;

    @Column(columnDefinition = "TEXT")
    private String encryptedPrivateKey4;

    @Column(columnDefinition = "TEXT")
    private String encryptedPrivateKey5;

    @Column(columnDefinition = "TEXT")
    private String encryptedPrivateKey6;

    @Column(columnDefinition = "TEXT")
    private String encryptedPrivateKey7;

    @Column(columnDefinition = "TEXT")
    private String encryptedPrivateKey8;

    @Column(columnDefinition = "TEXT")
    private String encryptedPrivateKey9;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "last_update")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserRegistration that)) return false;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
