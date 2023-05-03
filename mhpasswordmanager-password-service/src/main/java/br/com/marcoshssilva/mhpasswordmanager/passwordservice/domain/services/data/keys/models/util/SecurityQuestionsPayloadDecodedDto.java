package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class SecurityQuestionsPayloadDecodedDto implements Serializable {
    public static final long serialVersionUID = 1L;

    @JsonProperty(required = true)
    private String question;

    @JsonProperty(required = true, value = "expected_value")
    private String expectedValue;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SecurityQuestionsPayloadDecodedDto that)) return false;
        return Objects.equals(question, that.question) && Objects.equals(expectedValue, that.expectedValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(question, expectedValue);
    }

    @Override
    public String toString() {
        return "SecurityQuestionsPayloadDecodedDto{" +
                "question='" + question + '\'' +
                ", response='" + expectedValue + '\'' +
                '}';
    }
}
