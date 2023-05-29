package br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.web.data.models;

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
public class UserResetPasswordStep1Data implements Serializable {
    public static final long serialVersionUID = 1L;

    private String identification;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserResetPasswordStep1Data that)) return false;
        return Objects.equals(identification, that.identification);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identification);
    }
}
