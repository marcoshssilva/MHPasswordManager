package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@lombok.Builder
@lombok.Getter
@lombok.Setter
public class SecurityQuestionsPayloadDecodedDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Long id;

    @JsonProperty(required = true)
    private String question;

    @JsonProperty(required = true, value = "expected_value")
    private String expectedValue;

    @Schema(hidden = true)
    @JsonProperty(value = "created_at")
    private Date createdAt;

    @Schema(hidden = true)
    @JsonProperty(value = "update_last")
    private Date updateLast;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SecurityQuestionsPayloadDecodedDto that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(question, that.question) && Objects.equals(expectedValue, that.expectedValue) && Objects.equals(createdAt, that.createdAt) && Objects.equals(updateLast, that.updateLast);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, question, expectedValue, createdAt, updateLast);
    }
    @Override
    public String toString() {
        return "SecurityQuestionsPayloadDecodedDto{" +
                "id=" + id +
                ", question='" + question + '\'' +
                ", expectedValue='" + expectedValue + '\'' +
                ", createdAt=" + createdAt +
                ", updateLast=" + updateLast +
                '}';
    }
}
