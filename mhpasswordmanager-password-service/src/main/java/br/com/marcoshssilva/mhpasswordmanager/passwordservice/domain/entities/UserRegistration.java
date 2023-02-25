package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities;

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
@Entity
@Table(name = "users_registration")
@Builder
public class UserRegistration implements Serializable {
    public static final long serialVersionUID = 1L;

    @Id
    @Column(nullable = false, length = 36)
    @Getter
    @Setter
    private String id;

    @Column(nullable = false)
    @Getter
    @Setter
    private String email;

    @Column(columnDefinition = "TEXT")
    @Getter
    @Setter
    private String encodedPublicKey;

    @Column(columnDefinition = "TEXT")
    @Getter
    @Setter
    private String encryptedPrivateKeyWithPassword;

    @Column(columnDefinition = "TEXT")
    @Getter
    @Setter
    private String encryptedPrivateKey0;

    @Column(columnDefinition = "TEXT")
    @Getter
    @Setter
    private String encryptedPrivateKey1;

    @Column(columnDefinition = "TEXT")
    @Getter
    @Setter
    private String encryptedPrivateKey2;

    @Column(columnDefinition = "TEXT")
    @Getter
    @Setter
    private String encryptedPrivateKey3;

    @Column(columnDefinition = "TEXT")
    @Getter
    @Setter
    private String encryptedPrivateKey4;

    @Column(columnDefinition = "TEXT")
    @Getter
    @Setter
    private String encryptedPrivateKey5;

    @Column(columnDefinition = "TEXT")
    @Getter
    @Setter
    private String encryptedPrivateKey6;

    @Column(columnDefinition = "TEXT")
    @Getter
    @Setter
    private String encryptedPrivateKey7;

    @Column(columnDefinition = "TEXT")
    @Getter
    @Setter
    private String encryptedPrivateKey8;

    @Column(columnDefinition = "TEXT")
    @Getter
    @Setter
    private String encryptedPrivateKey9;

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
