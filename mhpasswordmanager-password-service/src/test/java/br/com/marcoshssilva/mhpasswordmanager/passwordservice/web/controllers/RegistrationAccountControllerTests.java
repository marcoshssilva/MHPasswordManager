package br.com.marcoshssilva.mhpasswordmanager.passwordservice.web.controllers;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.UserRegistrationService;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.exceptions.UserRegistrationNotFoundException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.models.UserRegisteredModel;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.web.error.RestControllerExceptionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class RegistrationAccountControllerTests {

    @Mock
    private UserRegistrationService userRegistrationService;

    private MockMvc mockMvc;

    private final Jwt mockJwt = new Jwt(
            "mock-token-value",
            Instant.now(),
            Instant.now().plusSeconds(3600),
            Map.of("alg", "none"),
            Map.of("sub", "user-alice")
    );

    @BeforeEach
    void setUp() {
        RegistrationAccountController controller = new RegistrationAccountController(userRegistrationService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new RestControllerExceptionManager())
                .setCustomArgumentResolvers(new HandlerMethodArgumentResolver() {
                    @Override
                    public boolean supportsParameter(MethodParameter parameter) {
                        return parameter.getParameterType().isAssignableFrom(Jwt.class);
                    }

                    @Override
                    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                        return mockJwt;
                    }
                })
                .build();
    }

    @DisplayName("Should return user registration data with status 200 OK")
    @Test
    void shouldReturnUserRegistrationData() throws Exception {
        UserRegisteredModel model = UserRegisteredModel.builder()
                .ownerName("user-alice")
                .buckets(Set.of("bucket-1", "bucket-2"))
                .build();

        when(userRegistrationService.getUserRegistration("user-alice")).thenReturn(model);

        mockMvc.perform(MockMvcRequestBuilders.get("/account/data"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ownerName").value("user-alice"))
                .andExpect(jsonPath("$.buckets.length()").value(2));
    }

    @DisplayName("Should return 404 NOT_FOUND when user registration not found")
    @Test
    void shouldReturnNotFoundWhenUserRegistrationNotFound() throws Exception {
        when(userRegistrationService.getUserRegistration("user-alice")).thenThrow(new UserRegistrationNotFoundException("User not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/account/data"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"));
    }
}
