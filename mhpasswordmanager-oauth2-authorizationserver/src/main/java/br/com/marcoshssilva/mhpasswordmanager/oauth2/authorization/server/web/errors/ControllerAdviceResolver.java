package br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.web.errors;

import br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.domain.constants.StatusTypeEnum;
import br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.domain.data.responses.HttpJsonResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class ControllerAdviceResolver {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<HttpJsonResponse<?>> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException exception, HttpServletRequest req, HttpServletResponse res) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(HttpJsonResponse.builder()
                        .status(StatusTypeEnum.ERROR)
                        .message(HttpStatus.BAD_GATEWAY.getReasonPhrase())
                        .errors(exception.getFieldErrors())
                        .build());
    }


}
