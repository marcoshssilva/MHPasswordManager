package br.com.marcoshssilva.mhpasswordmanager.oauth2server.web.errors;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.constants.StatusTypeEnum;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.exceptions.FailSendEmailException;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.web.data.responses.HttpJsonResponse;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.exceptions.CannotRegisterUserException;
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
    public ResponseEntity<HttpJsonResponse<Object>> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException exception, HttpServletRequest req, HttpServletResponse res) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(HttpJsonResponse.builder()
                        .status(StatusTypeEnum.ERROR)
                        .message(HttpStatus.BAD_GATEWAY.getReasonPhrase())
                        .errors(exception.getFieldErrors())
                        .build());
    }

    @ExceptionHandler(CannotRegisterUserException.class)
    public ResponseEntity<HttpJsonResponse<Object>> cannotRegisterUserExceptionHandler(CannotRegisterUserException exception, HttpServletRequest req, HttpServletResponse res) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(HttpJsonResponse.builder()
                        .status(StatusTypeEnum.ERROR)
                        .message(HttpStatus.BAD_GATEWAY.getReasonPhrase())
                        .error(exception.getMessage())
                        .build());
    }

    @ExceptionHandler(FailSendEmailException.class)
    public ResponseEntity<HttpJsonResponse<Object>> failSendEmailExceptionHandler(FailSendEmailException exception, HttpServletRequest req, HttpServletResponse res) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(HttpJsonResponse.builder()
                        .status(StatusTypeEnum.ERROR)
                        .message(HttpStatus.BAD_GATEWAY.getReasonPhrase())
                        .error(exception.getMessage())
                        .build());
    }
}
