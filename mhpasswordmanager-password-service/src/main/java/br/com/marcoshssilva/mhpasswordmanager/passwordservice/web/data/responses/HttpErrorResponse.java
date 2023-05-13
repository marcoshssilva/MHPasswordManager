package br.com.marcoshssilva.mhpasswordmanager.passwordservice.web.data.responses;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class HttpErrorResponse implements Serializable {
    public static final long serialVersionUID = 1L;

    private String path;
    private String error;
    private String message;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    private Date timestamp;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HttpErrorResponse that)) return false;
        return Objects.equals(path, that.path) && Objects.equals(error, that.error) && Objects.equals(timestamp, that.timestamp) && Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, error, timestamp, message);
    }

    @Override
    public String
    toString() {
        return "HttpErrorResponse{" +
                "path='" + path + '\'' +
                ", error='" + error + '\'' +
                ", timestamp=" + timestamp  +
                ", message='" + message + '\'' +
                '}';
    }
}
