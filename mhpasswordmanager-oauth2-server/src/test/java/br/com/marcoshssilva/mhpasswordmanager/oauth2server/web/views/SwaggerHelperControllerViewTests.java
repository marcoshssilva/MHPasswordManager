package br.com.marcoshssilva.mhpasswordmanager.oauth2server.web.views;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.configuration.AuthorizationConfigProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

class SwaggerHelperControllerViewTests {

    private MockMvc mockMvc;
    private AuthorizationConfigProperties properties;

    @BeforeEach
    void setUp() {
        properties = new AuthorizationConfigProperties();
        properties.setIssuerUri("http://localhost:12010");
        SwaggerHelperControllerView controller = new SwaggerHelperControllerView(properties);
        org.springframework.test.util.ReflectionTestUtils.setField(controller, "baseHref", "/");
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @DisplayName("Should return oauth2-redirect view with properties")
    @Test
    void shouldReturnOAuth2RedirectView() throws Exception {
        mockMvc.perform(get("/swagger-ui/redirect"))
                .andExpect(status().isOk())
                .andExpect(view().name("oauth2-redirect"))
                .andExpect(model().attributeExists("authorizationProperties"))
                .andExpect(model().attributeExists("baseHref"));
    }
}
