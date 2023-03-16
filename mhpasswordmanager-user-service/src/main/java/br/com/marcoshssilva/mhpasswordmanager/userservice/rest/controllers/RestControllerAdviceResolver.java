package br.com.marcoshssilva.mhpasswordmanager.userservice.rest.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class RestControllerAdviceResolver {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Void> noSuchElementExceptionResolver(NoSuchElementException e, HttpServletRequest req, HttpServletResponse res) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
