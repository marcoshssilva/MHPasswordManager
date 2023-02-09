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
    private String id;

    @Column(nullable = false)
    private String email;

    @Column(columnDefinition = "TEXT")
    private String encodedPublicKey;

    @Column(columnDefinition = "TEXT")
    private String encriptedPrivateKeyWithPassword;

    @Column(columnDefinition = "TEXT")
    private String[] encriptedPrivateKeys = new String[10];

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
        return encriptedPrivateKeyWithPassword;
    }

    public void setEncriptedPrivateKeyWithPassword(String encriptedPrivateKeyWithPassword) {
        this.encriptedPrivateKeyWithPassword = encriptedPrivateKeyWithPassword;
    }

    public String getEncriptedPrivateKey(int i) {
        return encriptedPrivateKeys[i];
    }

    public void setEncriptedPrivateKey(int i, String encriptedPrivateKey) {
        this.encriptedPrivateKeys[i] = encriptedPrivateKey;
    }

}
