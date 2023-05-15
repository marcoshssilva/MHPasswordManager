package br.com.marcoshssilva.mhpasswordmanager.emailservice.domain.models;

import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SimpleMailMessage implements Serializable {
    public static final long serialVersionUID = 1L;

    private String destination;
    private String subject;
    private String message;
    private Boolean isHtml;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimpleMailMessage that)) return false;
        return Objects.equals(destination, that.destination) && Objects.equals(subject, that.subject) && Objects.equals(message, that.message) && Objects.equals(isHtml, that.isHtml);
    }

    @Override
    public int hashCode() {
        return Objects.hash(destination, subject, message);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SimpleMailMessage{");
        sb.append("destination='").append(destination).append('\'');
        sb.append(", subject='").append(subject).append('\'');
        sb.append(", message='").append(message).append('\'');
        sb.append(", isHtml='").append(isHtml).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
