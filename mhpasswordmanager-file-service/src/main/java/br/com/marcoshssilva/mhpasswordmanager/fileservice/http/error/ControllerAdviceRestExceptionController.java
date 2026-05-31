package br.com.marcoshssilva.mhpasswordmanager.fileservice.http.error;

import br.com.marcoshssilva.mhpasswordmanager.fileservice.internal.exceptions.StorageErrorException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdviceRestExceptionController {
    @ExceptionHandler(StorageErrorException.class)
    public ResponseEntity<String> handleStorageErrorException(StorageErrorException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
