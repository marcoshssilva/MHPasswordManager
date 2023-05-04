package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Builder
public class PasswordPayloadDecodedDto implements Serializable {
    public static final long serialVersionUID = 1L;

    private Long id;
    @JsonProperty(required = true)
    private String password;

    @JsonProperty(required = true)
    private Boolean active;

    @JsonProperty(value = "created_at")
    private Date createdAt;
    @JsonProperty(value = "update_last")
    private Date updateLast;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PasswordPayloadDecodedDto that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(password, that.password) && Objects.equals(active, that.active) && Objects.equals(createdAt, that.createdAt) && Objects.equals(updateLast, that.updateLast);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, password, active, createdAt, updateLast);
    }

    @Override
    public String toString() {
        return "PasswordPayloadDecodedDto{" +
                "id=" + id +
                ", password='" + password + '\'' +
                ", active=" + active +
                ", createdAt=" + createdAt +
                ", updateLast=" + updateLast +
                '}';
    }
}
