package br.com.marcoshssilva.mhpasswordmanager.passwordservice.web.error;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt.exceptions.DecryptionException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt.exceptions.EncryptionException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.exceptions.BucketCannotBeCreatedException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.exceptions.BucketNotFoundException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.common.exceptions.ResultDataErrorException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.exceptions.KeyEncodedErrorConverterException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.exceptions.KeyNotFoundException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.exceptions.KeyRegistrationErrorException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.exceptions.UserAuthorizationCannotBeLoadedException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.exceptions.UserRegistrationNotFoundException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.web.data.responses.HttpErrorResponse;
import com.fasterxml.jackson.core.JsonParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;

class RestControllerExceptionManagerTests {

    private RestControllerExceptionManager exceptionManager;
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        exceptionManager = new RestControllerExceptionManager();
        request = new MockHttpServletRequest();
        request.setServletPath("/api/test");
    }

    @DisplayName("Should resolve KeyNotFoundException to 404 NOT_FOUND")
    @Test
    void shouldResolveKeyNotFoundException() {
        ResponseEntity<HttpErrorResponse> response = exceptionManager.keyNotFoundExceptionResolver(new KeyNotFoundException("Key missing"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Key missing");
        assertThat(response.getBody().getPath()).isEqualTo("/api/test");
    }

    @DisplayName("Should resolve UserRegistrationNotFoundException to 404 NOT_FOUND")
    @Test
    void shouldResolveUserRegistrationNotFoundException() {
        ResponseEntity<HttpErrorResponse> response = exceptionManager.userRegistrationNotFoundExceptionResolver(new UserRegistrationNotFoundException("User missing"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("User missing");
    }

    @DisplayName("Should resolve BucketNotFoundException to 404 NOT_FOUND")
    @Test
    void shouldResolveBucketNotFoundException() {
        ResponseEntity<HttpErrorResponse> response = exceptionManager.bucketNotFoundExceptionResolver(new BucketNotFoundException("Bucket missing"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Bucket missing");
    }

    @DisplayName("Should resolve JsonProcessingException to 500 INTERNAL_SERVER_ERROR")
    @Test
    void shouldResolveJsonProcessingException() {
        ResponseEntity<HttpErrorResponse> response = exceptionManager.jsonProcessingExceptionResolver(new JsonParseException(null, "JSON error"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).contains("JSON error");
    }

    @DisplayName("Should resolve KeyRegistrationErrorException to 400 BAD_REQUEST")
    @Test
    void shouldResolveKeyRegistrationErrorException() {
        ResponseEntity<HttpErrorResponse> response = exceptionManager.keyRegistrationExceptionResolver(new KeyRegistrationErrorException("Key register failed"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Key register failed");
    }

    @DisplayName("Should resolve KeyEncodedErrorConverterException to 400 BAD_REQUEST")
    @Test
    void shouldResolveKeyEncodedErrorConverterException() {
        ResponseEntity<HttpErrorResponse> response = exceptionManager.keyEncodedErrorConverterExceptionResolver(new KeyEncodedErrorConverterException("Convert failed"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Convert failed");
    }

    @DisplayName("Should resolve BucketCannotBeCreatedException to 400 BAD_REQUEST")
    @Test
    void shouldResolveBucketCannotBeCreatedException() {
        ResponseEntity<HttpErrorResponse> response = exceptionManager.bucketCannotBeCreatedExceptionResolver(new BucketCannotBeCreatedException("Bucket create error"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Bucket create error");
    }

    @DisplayName("Should resolve DecryptionException to 500 INTERNAL_SERVER_ERROR")
    @Test
    void shouldResolveDecryptionException() {
        ResponseEntity<HttpErrorResponse> response = exceptionManager.decryptionExceptionResolver(new DecryptionException("Decrypt failed", new RuntimeException()), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Decrypt failed");
    }

    @DisplayName("Should resolve EncryptionException to 500 INTERNAL_SERVER_ERROR")
    @Test
    void shouldResolveEncryptionException() {
        ResponseEntity<HttpErrorResponse> response = exceptionManager.encryptionExceptionResolver(new EncryptionException("Encrypt failed", new RuntimeException()), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Encrypt failed");
    }

    @DisplayName("Should resolve UserAuthorizationCannotBeLoadedException to 500 INTERNAL_SERVER_ERROR")
    @Test
    void shouldResolveUserAuthorizationCannotBeLoadedException() {
        ResponseEntity<HttpErrorResponse> response = exceptionManager.userAuthorizationCannotBeLoadedExceptionResolver(new UserAuthorizationCannotBeLoadedException("Auth failed"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Auth failed");
    }

    @DisplayName("Should resolve IllegalArgumentException to 500 INTERNAL_SERVER_ERROR")
    @Test
    void shouldResolveIllegalArgumentException() {
        ResponseEntity<HttpErrorResponse> response = exceptionManager.illegalArgumentExceptionResolver(new IllegalArgumentException("Illegal arg"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Illegal arg");
    }

    @DisplayName("Should resolve ResultDataErrorException by delegating to cause handler")
    @Test
    void shouldResolveResultDataErrorException() {
        ResultDataErrorException withKeyNotFound = new ResultDataErrorException("Key missing", new KeyNotFoundException("Key missing"));
        ResponseEntity<HttpErrorResponse> response1 = exceptionManager.resultDataErrorExceptionResolver(withKeyNotFound, request);
        assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        ResultDataErrorException withUserNotFound = new ResultDataErrorException("User missing", new UserRegistrationNotFoundException("User missing"));
        ResponseEntity<HttpErrorResponse> response2 = exceptionManager.resultDataErrorExceptionResolver(withUserNotFound, request);
        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        ResultDataErrorException withBucketNotFound = new ResultDataErrorException("Bucket missing", new BucketNotFoundException("Bucket missing"));
        ResponseEntity<HttpErrorResponse> response3 = exceptionManager.resultDataErrorExceptionResolver(withBucketNotFound, request);
        assertThat(response3.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        ResultDataErrorException withKeyRegError = new ResultDataErrorException("Key reg error", new KeyRegistrationErrorException("Key reg error"));
        ResponseEntity<HttpErrorResponse> response4 = exceptionManager.resultDataErrorExceptionResolver(withKeyRegError, request);
        assertThat(response4.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        ResultDataErrorException withKeyConvError = new ResultDataErrorException("Key conv error", new KeyEncodedErrorConverterException("Key conv error"));
        ResponseEntity<HttpErrorResponse> response5 = exceptionManager.resultDataErrorExceptionResolver(withKeyConvError, request);
        assertThat(response5.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        ResultDataErrorException withBucketCreateError = new ResultDataErrorException("Bucket error", new BucketCannotBeCreatedException("Bucket error"));
        ResponseEntity<HttpErrorResponse> response6 = exceptionManager.resultDataErrorExceptionResolver(withBucketCreateError, request);
        assertThat(response6.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        ResultDataErrorException withDecryptError = new ResultDataErrorException("Decrypt error", new DecryptionException("Decrypt error", new RuntimeException()));
        ResponseEntity<HttpErrorResponse> response7 = exceptionManager.resultDataErrorExceptionResolver(withDecryptError, request);
        assertThat(response7.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

        ResultDataErrorException withEncryptError = new ResultDataErrorException("Encrypt error", new EncryptionException("Encrypt error", new RuntimeException()));
        ResponseEntity<HttpErrorResponse> response8 = exceptionManager.resultDataErrorExceptionResolver(withEncryptError, request);
        assertThat(response8.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

        ResultDataErrorException withAuthError = new ResultDataErrorException("Auth error", new UserAuthorizationCannotBeLoadedException("Auth error"));
        ResponseEntity<HttpErrorResponse> response9 = exceptionManager.resultDataErrorExceptionResolver(withAuthError, request);
        assertThat(response9.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

        ResultDataErrorException withIllegalArg = new ResultDataErrorException("Illegal arg", new IllegalArgumentException("Illegal arg"));
        ResponseEntity<HttpErrorResponse> response10 = exceptionManager.resultDataErrorExceptionResolver(withIllegalArg, request);
        assertThat(response10.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

        ResultDataErrorException fallback = new ResultDataErrorException("Fallback error");
        ResponseEntity<HttpErrorResponse> response11 = exceptionManager.resultDataErrorExceptionResolver(fallback, request);
        assertThat(response11.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @DisplayName("Should resolve generic Exception to 500 INTERNAL_SERVER_ERROR")
    @Test
    void shouldResolveGenericException() {
        ResponseEntity<HttpErrorResponse> response = exceptionManager.exceptionResolver(new Exception("Generic error"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Generic error");
    }
}
