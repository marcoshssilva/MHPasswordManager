package br.com.marcoshssilva.mhpasswordmanager.passwordservice.web.data.requests;

import javax.validation.constraints.NotBlank;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@lombok.Getter
@lombok.Setter
@lombok.NoArgsConstructor
@lombok.ToString
public class SimpleBucketCryptKeyRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank
    private String base64Data;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SimpleBucketCryptKeyRequest that = (SimpleBucketCryptKeyRequest) o;
        return Objects.equals(base64Data, that.base64Data);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(base64Data);
    }
}
