package br.com.marcoshssilva.mhpasswordmanager.oauth2server.web.data.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Objects;

@NoArgsConstructor
@ToString
@Getter
@Setter
public class UserResetPasswordStep2Data implements Serializable {
    public static final long serialVersionUID = 1L;

    private String code;
    private String password;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserResetPasswordStep2Data that)) return false;
        return Objects.equals(code, that.code) && Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, password);
    }
}
