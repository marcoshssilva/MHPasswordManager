package br.com.marcoshssilva.mhpasswordmanager.userservice.http.error;

import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.exceptions.AlreadyExistsInDatabaseException;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.exceptions.ElementNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RestControllerAdviceResolverTests {

    private final RestControllerAdviceResolver resolver = new RestControllerAdviceResolver();

    @DisplayName("Should resolve ElementNotFoundException with 404 NOT FOUND")
    @Test
    void shouldResolveElementNotFoundException() {
        ResponseEntity<Void> response = resolver.noSuchElementExceptionResolver(
                new ElementNotFoundException(),
                new MockHttpServletRequest(),
                new MockHttpServletResponse()
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @DisplayName("Should resolve AlreadyExistsInDatabaseException with 400 BAD REQUEST")
    @Test
    void shouldResolveAlreadyExistsInDatabaseException() {
        ResponseEntity<Void> response = resolver.alreadyExistsInDatabaseExceptionResolver(
                new AlreadyExistsInDatabaseException(),
                new MockHttpServletRequest(),
                new MockHttpServletResponse()
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
