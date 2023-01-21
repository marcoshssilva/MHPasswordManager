package br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.web.data.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.domain.constants.StatusTypeEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.validation.FieldError;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@JsonInclude(Include.NON_NULL)
public class HttpJsonResponse<T> {
    StatusTypeEnum status;
    private String message;
    private List<FieldError> errors;
    private String error;
    private T data;
}
