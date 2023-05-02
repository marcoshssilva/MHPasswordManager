package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.util;

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

    private String question;
    private String response;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SecurityQuestionsPayloadDecodedDto that)) return false;
        return Objects.equals(question, that.question) && Objects.equals(response, that.response);
    }

    @Override
    public int hashCode() {
        return Objects.hash(question, response);
    }

    @Override
    public String toString() {
        return "SecurityQuestionsPayloadDecodedDto{" +
                "question='" + question + '\'' +
                ", response='" + response + '\'' +
                '}';
    }
}
