package br.com.marcoshssilva.mhpasswordmanager.userservice.http.data.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class AccountRequestValidatePasswordModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String password;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AccountRequestValidatePasswordModel that = (AccountRequestValidatePasswordModel) o;
        return Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(password);
    }
}
