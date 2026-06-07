package br.com.marcoshssilva.mhpasswordmanager.userservice.http.data.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Data
@AllArgsConstructor
@ToString
@Builder
public class AccountResponseValidatePasswordData implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Boolean isValid;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AccountResponseValidatePasswordData that = (AccountResponseValidatePasswordData) o;
        return Objects.equals(isValid, that.isValid);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(isValid);
    }
}
