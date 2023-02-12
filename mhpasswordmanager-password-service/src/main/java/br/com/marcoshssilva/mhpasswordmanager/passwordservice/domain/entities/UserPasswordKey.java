package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.enums.PasswordKeyTypesEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
@Table(schema = "db_passwords", name = "users_keys")
@Builder
public class UserPasswordKey implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_registration_id", nullable = false)
    private UserRegistration userRegistration;

    @ElementCollection
    @CollectionTable(schema = "db_passwords", name = "users_key_tags")
    @Column(nullable = false)
    private Collection<String> tags = Collections.emptySet();

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
        if (!(o instanceof UserPasswordKey userPasswordKey)) return false;
        return id.equals(userPasswordKey.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserRegistration getUserRegistration() {
        return userRegistration;
    }

    public void setUserRegistration(UserRegistration userRegistration) {
        this.userRegistration = userRegistration;
    }

    public Collection<String> getTags() {
        return tags;
    }

    public void setTags(Collection<String> tags) {
        this.tags = tags;
    }

    public PasswordKeyTypesEnum getType() {
        return type;
    }

    public void setType(PasswordKeyTypesEnum type) {
        this.type = type;
    }

    public Date getCreateAt() {
        return createdAt;
    }

    public void setCreateAt(Date createAt) {
        this.createdAt = createAt;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
