package br.com.marcoshssilva.mhpasswordmanager.passwordservice.web.data.requests;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@lombok.Getter
@lombok.Setter
@lombok.NoArgsConstructor
@lombok.ToString
public class RsaCryptKeyRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank
    private String base64Data;
    @NotBlank
    @Size(min = 10, max = 36)
    private String secret;
    @NotBlank
    @Size(min = 36, max = 36)
    private String bucketUuid;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RsaCryptKeyRequest that)) return false;
        return Objects.equals(base64Data, that.base64Data) && Objects.equals(secret, that.secret) && Objects.equals(bucketUuid, that.bucketUuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(base64Data, secret, bucketUuid);
    }
}
