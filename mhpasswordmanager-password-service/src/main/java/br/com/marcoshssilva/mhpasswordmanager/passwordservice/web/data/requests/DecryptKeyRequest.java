package br.com.marcoshssilva.mhpasswordmanager.passwordservice.web.data.requests;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class DecryptKeyRequest implements Serializable {
    public static final long serialVersionUID = 1L;

    @NotBlank
    private String base64Data;
    @NotBlank
    @Size(min = 10, max = 36)
    private String secret;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DecryptKeyRequest that)) return false;
        return Objects.equals(base64Data, that.base64Data) && Objects.equals(secret, that.secret);
    }

    @Override
    public int hashCode() {
        return Objects.hash(base64Data, secret);
    }
}
