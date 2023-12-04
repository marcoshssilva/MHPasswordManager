package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models;

import lombok.*;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisteredUserKeyVerificationMailMessage {
    private String code;
    private String name;
    private String email;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegisteredUserKeyVerificationMailMessage that = (RegisteredUserKeyVerificationMailMessage) o;
        return Objects.equals(code, that.code) && Objects.equals(name, that.name) && Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, name, email);
    }

    @Override
    public String toString() {
        return "RegisteredUserKeyVerificationMailMessage{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
