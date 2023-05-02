package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.enums.PasswordKeyTypesEnum;
import lombok.*;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public final class KeyPayloadEncodedDto implements Serializable {
    public static final long serialVersionUID = 1L;

    private Long id;
    private String owner;
    private String[] tags;

    private KeyStorePayloadEncodedDto[] encodedKeys;
    private PasswordKeyTypesEnum type;

    private Date createdAt;
    private Date lastUpdate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KeyPayloadEncodedDto that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(owner, that.owner) && Arrays.equals(tags, that.tags) && Arrays.equals(encodedKeys, that.encodedKeys) && type == that.type && Objects.equals(createdAt, that.createdAt) && Objects.equals(lastUpdate, that.lastUpdate);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, owner, type, createdAt, lastUpdate);
        result = 31 * result + Arrays.hashCode(tags);
        result = 31 * result + Arrays.hashCode(encodedKeys);
        return result;
    }

    @Override
    public String toString() {
        return "KeyPayloadEncodedDto{" +
                "id=" + id +
                ", owner='" + owner + '\'' +
                ", tags=" + Arrays.toString(tags) +
                ", encodedKeys=" + Arrays.toString(encodedKeys) +
                ", type=" + type +
                ", createdAt=" + createdAt +
                ", lastUpdate=" + lastUpdate +
                '}';
    }
}
