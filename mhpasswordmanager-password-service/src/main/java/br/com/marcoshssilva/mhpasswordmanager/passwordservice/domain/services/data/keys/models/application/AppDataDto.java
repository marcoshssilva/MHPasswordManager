package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.application;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.util.PasswordPayloadDecodedDto;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Builder
public class AppDataDto implements Serializable {
    public static final long serialVersionUID = 1L;

    private String username;
    private Set<PasswordPayloadDecodedDto> storedPasswords;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AppDataDto that)) return false;
        return Objects.equals(username, that.username) && Objects.equals(storedPasswords, that.storedPasswords);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, storedPasswords);
    }

    @Override
    public String toString() {
        return "AppDataDto{" +
                "username='" + username + '\'' +
                ", storedPasswords=" + storedPasswords +
                '}';
    }
}
