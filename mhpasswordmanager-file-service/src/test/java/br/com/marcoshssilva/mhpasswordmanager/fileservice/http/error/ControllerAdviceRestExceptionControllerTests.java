package br.com.marcoshssilva.mhpasswordmanager.fileservice.http.error;

import br.com.marcoshssilva.mhpasswordmanager.fileservice.internal.exceptions.StorageErrorException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class ControllerAdviceRestExceptionControllerTests {

    @DisplayName("Should return BAD_REQUEST with exception message")
    @Test
    void shouldHandleStorageErrorException() {
        ControllerAdviceRestExceptionController advice = new ControllerAdviceRestExceptionController();
        StorageErrorException exception = new StorageErrorException("Custom storage error");

        ResponseEntity<String> response = advice.handleStorageErrorException(exception);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Custom storage error");
    }
}
