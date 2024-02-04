package br.com.marcoshssilva.mhpasswordmanager.passwordservice.web.error;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt.exceptions.DecryptionException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt.exceptions.EncryptionException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.exceptions.BucketCannotBeCreatedException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.exceptions.BucketNotFoundException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.exceptions.KeyNotFoundException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.exceptions.KeyRegistrationErrorException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.exceptions.KeyEncodedErrorConverterException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.exceptions.UserAuthorizationCannotBeLoadedException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.exceptions.UserRegistrationNotFoundException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.web.data.responses.HttpErrorResponse;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@RestControllerAdvice
public class RestControllerExceptionManager {

    @ExceptionHandler(KeyNotFoundException.class)
    public ResponseEntity<HttpErrorResponse> keyNotFoundExceptionResolver(KeyNotFoundException e, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                HttpErrorResponse.builder()
                        .message(e.getMessage()).timestamp(new Date()).path(req.getServletPath())
                        .build());
    }

    @ExceptionHandler(UserRegistrationNotFoundException.class)
    public ResponseEntity<HttpErrorResponse> userRegistrationNotFoundExceptionResolver(UserRegistrationNotFoundException e, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                HttpErrorResponse.builder()
                        .message(e.getMessage()).timestamp(new Date()).path(req.getServletPath())
                        .build());
    }

    @ExceptionHandler(BucketNotFoundException.class)
    public ResponseEntity<HttpErrorResponse> bucketNotFoundExceptionResolver(BucketNotFoundException e, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                HttpErrorResponse.builder()
                        .message(e.getMessage()).timestamp(new Date()).path(req.getServletPath())
                        .build());
    }

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<HttpErrorResponse> jsonProcessingExceptionResolver(JsonProcessingException e, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                HttpErrorResponse.builder()
                        .message(e.getMessage()).timestamp(new Date()).path(req.getServletPath())
                        .build());
    }

    @ExceptionHandler(KeyRegistrationErrorException.class)
    public ResponseEntity<HttpErrorResponse> keyRegistrationExceptionResolver(KeyRegistrationErrorException e, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                HttpErrorResponse.builder()
                        .message(e.getMessage()).timestamp(new Date()).path(req.getServletPath())
                        .build());
    }

    @ExceptionHandler(KeyEncodedErrorConverterException.class)
    public ResponseEntity<HttpErrorResponse> keyEncodedErrorConverterExceptionResolver(KeyEncodedErrorConverterException e, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                HttpErrorResponse.builder()
                        .message(e.getMessage()).timestamp(new Date()).path(req.getServletPath())
                        .build());
    }

    @ExceptionHandler(BucketCannotBeCreatedException.class)
    public ResponseEntity<HttpErrorResponse> bucketCannotBeCreatedExceptionResolver(BucketCannotBeCreatedException e, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                HttpErrorResponse.builder()
                        .message(e.getMessage()).timestamp(new Date()).path(req.getServletPath())
                        .build());
    }

    @ExceptionHandler(DecryptionException.class)
    public ResponseEntity<HttpErrorResponse> decryptionExceptionResolver(DecryptionException e, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                HttpErrorResponse.builder()
                        .message(e.getMessage()).timestamp(new Date()).path(req.getServletPath())
                        .build());
    }

    @ExceptionHandler(EncryptionException.class)
    public ResponseEntity<HttpErrorResponse> encryptionExceptionResolver(EncryptionException e, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                HttpErrorResponse.builder()
                        .message(e.getMessage()).timestamp(new Date()).path(req.getServletPath())
                        .build());
    }

    @ExceptionHandler(UserAuthorizationCannotBeLoadedException.class)
    public ResponseEntity<HttpErrorResponse> userAuthorizationCannotBeLoadedExceptionResolver(UserAuthorizationCannotBeLoadedException e, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                HttpErrorResponse.builder()
                        .message(e.getMessage()).timestamp(new Date()).path(req.getServletPath())
                        .build());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<HttpErrorResponse> illegalArgumentExceptionResolver(IllegalArgumentException e, HttpServletRequest req){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                HttpErrorResponse.builder()
                        .message(e.getMessage()).timestamp(new Date()).path(req.getServletPath())
                        .build());
    }
}
