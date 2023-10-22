package br.com.marcoshssilva.mhpasswordmanager.userservice.domain.entities;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "users_recovery_password_code")
@Entity
@Getter
@Setter
@Builder
public class AccountRecoveryPasswordCode implements Serializable {
    public static final long serialVersionUID = 1L;

    @EmbeddedId
    private AccountRecoveryPasswordCodePK id;

    @Column(name = "user_agent_client")
    private String userAgentClient;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "expires_at")
    private Date expiresAt;

    @Column(name = "completed")
    private Boolean completed;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountRecoveryPasswordCode that = (AccountRecoveryPasswordCode) o;
        return Objects.equals(id, that.id) && Objects.equals(userAgentClient, that.userAgentClient) && Objects.equals(createdAt, that.createdAt) && Objects.equals(expiresAt, that.expiresAt) && Objects.equals(completed, that.completed);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userAgentClient, createdAt, expiresAt, completed);
    }
}
