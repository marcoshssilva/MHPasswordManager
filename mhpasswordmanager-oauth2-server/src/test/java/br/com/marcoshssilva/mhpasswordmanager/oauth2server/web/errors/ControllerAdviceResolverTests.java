package br.com.marcoshssilva.mhpasswordmanager.oauth2server.web.errors;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.constants.StatusTypeEnum;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.exceptions.BusinessRuleException;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.exceptions.CannotRegisterUserException;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.exceptions.FailSendEmailException;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.web.data.responses.HttpJsonResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ControllerAdviceResolverTests {

    private ControllerAdviceResolver resolver;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        resolver = new ControllerAdviceResolver();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    public void dummyMethod(String param) {}

    @DisplayName("Should handle MethodArgumentNotValidException")
    @Test
    void shouldHandleMethodArgumentNotValidException() throws Exception {
        Method method = this.getClass().getMethod("dummyMethod", String.class);
        MethodParameter parameter = new MethodParameter(method, 0);
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "target");
        bindingResult.addError(new FieldError("target", "field1", "default message"));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<HttpJsonResponse<Object>> result = resolver.methodArgumentNotValidExceptionHandler(ex, request, response);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(StatusTypeEnum.ERROR, result.getBody().getStatus());
        assertEquals(1, result.getBody().getErrors().size());
    }

    @DisplayName("Should handle CannotRegisterUserException")
    @Test
    void shouldHandleCannotRegisterUserException() {
        CannotRegisterUserException ex = new CannotRegisterUserException("Cannot register user");

        ResponseEntity<HttpJsonResponse<Object>> result = resolver.cannotRegisterUserExceptionHandler(ex, request, response);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(StatusTypeEnum.ERROR, result.getBody().getStatus());
        assertEquals("Cannot register user", result.getBody().getError());
    }

    @DisplayName("Should handle FailSendEmailException")
    @Test
    void shouldHandleFailSendEmailException() {
        FailSendEmailException ex = new FailSendEmailException("Failed to send email");

        ResponseEntity<HttpJsonResponse<Object>> result = resolver.failSendEmailExceptionHandler(ex, request, response);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(StatusTypeEnum.ERROR, result.getBody().getStatus());
        assertEquals("Failed to send email", result.getBody().getError());
    }

    @DisplayName("Should handle BusinessRuleException")
    @Test
    void shouldHandleBusinessRuleException() {
        BusinessRuleException ex = new BusinessRuleException("Business rule violated");

        ResponseEntity<HttpJsonResponse<Void>> result = resolver.businessRuleExceptionHandler(ex, request, response);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(StatusTypeEnum.ERROR, result.getBody().getStatus());
        assertEquals("Business rule violated", result.getBody().getError());
    }
}
