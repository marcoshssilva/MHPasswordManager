package br.com.marcoshssilva.mhpasswordmanager.oauth2server.web.views;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.configuration.AuthorizationConfigProperties;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.exceptions.BusinessRuleException;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.UserService;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.web.errors.ControllerAdviceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(MockitoExtension.class)
class HomeAndLoginControllerViewTests {

    @Mock
    private UserService userService;

    private AuthorizationConfigProperties authorizationProperties;
    private UserDetails userDetails;

    private MockMvc createMockMvc(UserDetails principalToInject) {
        HomeAndLoginControllerView controller = new HomeAndLoginControllerView(authorizationProperties, userService);
        org.springframework.test.util.ReflectionTestUtils.setField(controller, "baseHref", "/");

        org.springframework.web.servlet.view.InternalResourceViewResolver viewResolver = new org.springframework.web.servlet.view.InternalResourceViewResolver();
        viewResolver.setPrefix("/templates/");
        viewResolver.setSuffix(".html");

        return MockMvcBuilders.standaloneSetup(controller)
                .setViewResolvers(viewResolver)
                .setControllerAdvice(new ControllerAdviceResolver())
                .setCustomArgumentResolvers(new HandlerMethodArgumentResolver() {
                    @Override
                    public boolean supportsParameter(MethodParameter parameter) {
                        return parameter.hasParameterAnnotation(AuthenticationPrincipal.class);
                    }

                    @Override
                    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                        return principalToInject;
                    }
                })
                .build();
    }

    @BeforeEach
    void setUp() {
        authorizationProperties = new AuthorizationConfigProperties();
        authorizationProperties.setIssuerUri("http://localhost:12010");
        userDetails = new User("testuser", "password", Collections.emptyList());
    }

    @DisplayName("Should return login view when user is not authenticated")
    @Test
    void shouldReturnLoginViewWhenUserNotAuthenticated() throws Exception {
        MockMvc mockMvc = createMockMvc(null);

        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("authorizationProperties"));
    }

    @DisplayName("Should redirect to root when user is already authenticated on /login")
    @Test
    void shouldRedirectWhenUserAlreadyAuthenticatedOnLogin() throws Exception {
        MockMvc mockMvc = createMockMvc(userDetails);

        mockMvc.perform(get("/login"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/*"));
    }

    @DisplayName("Should return home view when accessing index")
    @Test
    void shouldReturnHomeView() throws Exception {
        MockMvc mockMvc = createMockMvc(userDetails);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeExists("userDetails"))
                .andExpect(model().attributeExists("authorizationProperties"));
    }

    @DisplayName("Should verify account successfully and return verify-account view")
    @Test
    void shouldVerifyAccountSuccessfully() throws Exception {
        MockMvc mockMvc = createMockMvc(null);
        doNothing().when(userService).verifyUserAccount(eq("valid-code"), any());

        mockMvc.perform(get("/verify/{code}", "valid-code")
                        .header("User-Agent", "JUnit-Agent"))
                .andExpect(status().isOk())
                .andExpect(view().name("verify-account"))
                .andExpect(model().attributeExists("browserParams"));
    }

    @DisplayName("Should handle error when verify account fails")
    @Test
    void shouldHandleErrorWhenVerifyAccountFails() throws Exception {
        MockMvc mockMvc = createMockMvc(null);
        doThrow(new BusinessRuleException("Verification code expired"))
                .when(userService).verifyUserAccount(eq("expired-code"), any());

        mockMvc.perform(get("/verify/{code}", "expired-code"))
                .andExpect(status().isBadRequest());
    }
}
