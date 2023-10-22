package br.com.marcoshssilva.mhpasswordmanager.userservice.domain.entities;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Embeddable
public class AccountRecoveryPasswordCodePK implements Serializable {
    public static final long serialVersionUID = 1L;

    @Column(name = "code")
    private String code;

    @Column(name = "username")
    private String username;

    @Column(name = "ip_client")
    private String ipClient;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountRecoveryPasswordCodePK that = (AccountRecoveryPasswordCodePK) o;
        return Objects.equals(code, that.code) && Objects.equals(username, that.username) && Objects.equals(ipClient, that.ipClient);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, username, ipClient);
    }
}
