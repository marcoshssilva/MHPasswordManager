package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder
public class UserRegisteredModel implements Serializable {
    public static final long serialVersionUID = 1L;

    private String uuid;
    private String publicKey;
    private Map<String, String> keys;
    private String keyAlg;
    @JsonProperty(value = "created_at")
    private Date createdAt;
    @JsonProperty(value = "last_update")
    private Date lastUpdate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserRegisteredModel that)) return false;
        return Objects.equals(uuid, that.uuid) && Objects.equals(publicKey, that.publicKey) && Objects.equals(keys, that.keys) && Objects.equals(keyAlg, that.keyAlg) && Objects.equals(createdAt, that.createdAt) && Objects.equals(lastUpdate, that.lastUpdate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, publicKey, keys, keyAlg, createdAt, lastUpdate);
    }
    @Override
    public String
    toString() {
        return "UserRegisteredModel{" +
                "uuid='" + uuid + '\'' +
                ", publicKey='" + publicKey + '\'' +
                ", keys=" + keys +
                ", keyAlg='" + keyAlg + '\'' +
                ", createdAt=" + createdAt +
                ", lastUpdate=" + lastUpdate +
                '}';
    }
}
