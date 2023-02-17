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

    public void setEncryptedPrivateKeyWithPassword(String encryptedPrivateKeyWithPassword) {
        this.encryptedPrivateKeyWithPassword = encryptedPrivateKeyWithPassword;
    }

    public String getEncryptedPrivateKey0() {
        return encryptedPrivateKey0;
    }

    public void setEncryptedPrivateKey0(String encryptedPrivateKey0) {
        this.encryptedPrivateKey0 = encryptedPrivateKey0;
    }

    public String getEncryptedPrivateKey1() {
        return encryptedPrivateKey1;
    }

    public void setEncryptedPrivateKey1(String encryptedPrivateKey1) {
        this.encryptedPrivateKey1 = encryptedPrivateKey1;
    }

    public String getEncryptedPrivateKey2() {
        return encryptedPrivateKey2;
    }

    public void setEncryptedPrivateKey2(String encryptedPrivateKey2) {
        this.encryptedPrivateKey2 = encryptedPrivateKey2;
    }

    public String getEncryptedPrivateKey3() {
        return encryptedPrivateKey3;
    }

    public void setEncryptedPrivateKey3(String encryptedPrivateKey3) {
        this.encryptedPrivateKey3 = encryptedPrivateKey3;
    }

    public String getEncryptedPrivateKey4() {
        return encryptedPrivateKey4;
    }

    public void setEncryptedPrivateKey4(String encryptedPrivateKey4) {
        this.encryptedPrivateKey4 = encryptedPrivateKey4;
    }

    public String getEncryptedPrivateKey5() {
        return encryptedPrivateKey5;
    }

    public void setEncryptedPrivateKey5(String encryptedPrivateKey5) {
        this.encryptedPrivateKey5 = encryptedPrivateKey5;
    }

    public String getEncryptedPrivateKey6() {
        return encryptedPrivateKey6;
    }

    public void setEncryptedPrivateKey6(String encryptedPrivateKey6) {
        this.encryptedPrivateKey6 = encryptedPrivateKey6;
    }

    public String getEncryptedPrivateKey7() {
        return encryptedPrivateKey7;
    }

    public void setEncryptedPrivateKey7(String encryptedPrivateKey7) {
        this.encryptedPrivateKey7 = encryptedPrivateKey7;
    }

    public String getEncryptedPrivateKey8() {
        return encryptedPrivateKey8;
    }

    public void setEncryptedPrivateKey8(String encryptedPrivateKey8) {
        this.encryptedPrivateKey8 = encryptedPrivateKey8;
    }

    public String getEncryptedPrivateKey9() {
        return encryptedPrivateKey9;
    }

    public void setEncryptedPrivateKey9(String encryptedPrivateKey9) {
        this.encryptedPrivateKey9 = encryptedPrivateKey9;
    }
}
