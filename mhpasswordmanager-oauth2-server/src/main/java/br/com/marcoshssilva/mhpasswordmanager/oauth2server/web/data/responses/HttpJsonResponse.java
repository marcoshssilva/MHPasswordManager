package br.com.marcoshssilva.mhpasswordmanager.oauth2server.web.data.responses;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.constants.StatusTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
