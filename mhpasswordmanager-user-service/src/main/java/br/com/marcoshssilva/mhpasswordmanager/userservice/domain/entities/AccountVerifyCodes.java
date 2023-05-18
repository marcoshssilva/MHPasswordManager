package br.com.marcoshssilva.mhpasswordmanager.userservice.domain.entities;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "users_verify_codes")
@Entity
@Getter
@Setter
@Builder
public class AccountVerifyCodes implements Serializable {
    public static final long serialVersionUID = 1L;

    @Id
    @Column(name = "uuid_code")
    private String code;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "ip_client")
    private String ip;

    @Column(name = "user_agent_client")
    private String userAgent;

    @Column(name = "created_at")
    private Date createdAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccountVerifyCodes that)) return false;
        return Objects.equals(code, that.code) && Objects.equals(username, that.username) && Objects.equals(ip, that.ip) && Objects.equals(userAgent, that.userAgent) && Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, username, ip, userAgent, createdAt);
    }
}
