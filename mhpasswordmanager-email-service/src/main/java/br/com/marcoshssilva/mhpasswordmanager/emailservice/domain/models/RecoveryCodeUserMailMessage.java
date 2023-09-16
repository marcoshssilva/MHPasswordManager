package br.com.marcoshssilva.mhpasswordmanager.emailservice.domain.models;

import lombok.*;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecoveryCodeUserMailMessage {
    private String code;
    private String name;
    private String email;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecoveryCodeUserMailMessage that = (RecoveryCodeUserMailMessage) o;
        return Objects.equals(code, that.code) && Objects.equals(name, that.name) && Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, name, email);
    }

    @Override
    public String toString() {
        return "RecoveryCodeUserMailMessage{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
