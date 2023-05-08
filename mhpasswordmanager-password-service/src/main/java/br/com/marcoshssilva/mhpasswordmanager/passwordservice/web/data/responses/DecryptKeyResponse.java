package br.com.marcoshssilva.mhpasswordmanager.passwordservice.web.data.responses;

import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class DecryptKeyResponse implements Serializable {
    public static final long serialVersionUID = 1L;

    private String data;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DecryptKeyResponse that)) return false;
        return Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }
}
