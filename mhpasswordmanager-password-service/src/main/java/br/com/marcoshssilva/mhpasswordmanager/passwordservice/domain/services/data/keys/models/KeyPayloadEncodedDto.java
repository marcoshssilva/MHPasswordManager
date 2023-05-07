package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities.UserPasswordKey;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities.UserPasswordKeyType;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities.UserRegistration;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.enums.PasswordKeyTypesEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public final class KeyPayloadEncodedDto implements Serializable {
    public static final long serialVersionUID = 1L;

    private Long id;
    @JsonProperty(value = "owner_id")
    private String ownerId;
    private String description;
    private String[] tags;

    @JsonProperty(value = "encoded_keys")
    private KeyStorePayloadEncodedDto[] encodedKeys;
    private PasswordKeyTypesEnum type;

    @JsonProperty(value = "created_at")
    private Date createdAt;
    @JsonProperty(value = "last_update")
    private Date lastUpdate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KeyPayloadEncodedDto that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(ownerId, that.ownerId) && Objects.equals(description, that.description) && Arrays.equals(tags, that.tags) && Arrays.equals(encodedKeys, that.encodedKeys) && type == that.type && Objects.equals(createdAt, that.createdAt) && Objects.equals(lastUpdate, that.lastUpdate);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, ownerId, description, type, createdAt, lastUpdate);
        result = 31 * result + Arrays.hashCode(tags);
        result = 31 * result + Arrays.hashCode(encodedKeys);
        return result;
    }

    @Override
    public String toString() {
        return "KeyPayloadEncodedDto{" +
                "id=" + id +
                ", ownerId='" + ownerId + '\'' +
                ", description='" + description + '\'' +
                ", tags=" + Arrays.toString(tags) +
                ", encodedKeys=" + Arrays.toString(encodedKeys) +
                ", type=" + type +
                ", createdAt=" + createdAt +
                ", lastUpdate=" + lastUpdate +
                '}';
    }

    public UserPasswordKey toEntity() {
        return UserPasswordKey.builder()
            .description(this.description)
            .tags(List.of(this.tags))
            .type(UserPasswordKeyType.builder().id(this.type.getId().longValue()).build())
            .userRegistration(UserRegistration.builder().id(this.ownerId).build())
            .createdAt(this.createdAt)
            .lastUpdate(this.lastUpdate)
            .build();
    }
}
