package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities.UserPasswordStoredValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
public final class KeyStorePayloadEncodedDto implements Serializable {
    public static final long serialVersionUID = 1L;

    private Long id;
    private String data;
    @JsonProperty(value = "created_at")
    private LocalDateTime createdAt;
    @JsonProperty(value = "last_update")
    private LocalDateTime lastUpdate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KeyStorePayloadEncodedDto that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(data, that.data) && Objects.equals(createdAt, that.createdAt) && Objects.equals(lastUpdate, that.lastUpdate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, data, createdAt, lastUpdate);
    }

    @Override
    public String toString() {
        return "KeyStorePayloadEncodedDto{" +
                "id=" + id +
                ", data='" + data + '\'' +
                ", createdAt=" + createdAt +
                ", lastUpdate=" + lastUpdate +
                '}';
    }

    public UserPasswordStoredValue toEntity() {
        return UserPasswordStoredValue.builder()
                .data(this.data)
                .id(this.id)
                .createdAt(this.createdAt)
                .lastUpdate(this.lastUpdate)
                .build();
    }
}
