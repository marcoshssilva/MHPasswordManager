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
@Table(schema = "db_passwords", name = "users_registration")
@Builder
public class UserRegistration implements Serializable {
    private static final long serialVersionUID = 1L;

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
    private String[] encryptedPrivateKeys = new String[10];

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserRegistration)) return false;
        UserRegistration that = (UserRegistration) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEncodedPublicKey() {
        return encodedPublicKey;
    }

    public void setEncodedPublicKey(String encodedPublicKey) {
        this.encodedPublicKey = encodedPublicKey;
    }

    public String getEncriptedPrivateKeyWithPassword() {
        return encryptedPrivateKeyWithPassword;
    }

    public void setEncriptedPrivateKeyWithPassword(String encryptedPrivateKeyWithPassword) {
        this.encryptedPrivateKeyWithPassword = encryptedPrivateKeyWithPassword;
    }

    public String getEncriptedPrivateKey(int i) {
        return encryptedPrivateKeys[i];
    }

    public void setEncriptedPrivateKey(int i, String encryptedPrivateKey) {
        this.encryptedPrivateKeys[i] = encryptedPrivateKey;
    }

}
