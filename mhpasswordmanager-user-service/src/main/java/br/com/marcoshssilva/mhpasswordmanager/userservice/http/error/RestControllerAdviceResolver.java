package br.com.marcoshssilva.mhpasswordmanager.userservice.http.error;

import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.exceptions.AlreadyExistsInDatabaseException;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.exceptions.ElementNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestControllerAdvice
public class RestControllerAdviceResolver {
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Void> uploadTooLarge() {
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).build();
    }

    @ExceptionHandler(ElementNotFoundException.class)
    public ResponseEntity<Void> noSuchElementExceptionResolver(ElementNotFoundException e, HttpServletRequest req, HttpServletResponse res) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @ExceptionHandler(AlreadyExistsInDatabaseException.class)
    public ResponseEntity<Void> alreadyExistsInDatabaseExceptionResolver(AlreadyExistsInDatabaseException e, HttpServletRequest req, HttpServletResponse res) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}
