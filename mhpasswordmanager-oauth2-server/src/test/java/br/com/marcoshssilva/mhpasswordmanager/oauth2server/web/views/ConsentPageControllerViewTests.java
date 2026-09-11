package br.com.marcoshssilva.mhpasswordmanager.oauth2server.web.views;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

class ConsentPageControllerViewTests {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        ConsentPageControllerView controller = new ConsentPageControllerView();
        org.springframework.test.util.ReflectionTestUtils.setField(controller, "baseHref", "/");
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @DisplayName("Should return consent-page view with populated attributes")
    @Test
    void shouldReturnConsentPageView() throws Exception {
        Principal principal = () -> "testuser";

        mockMvc.perform(get("/oauth2/consent")
                        .principal(principal)
                        .param("scope", "email profile custom_scope")
                        .param("client_id", "MHPasswordManager")
                        .param("state", "state123"))
                .andExpect(status().isOk())
                .andExpect(view().name("consent-page"))
                .andExpect(model().attribute("clientId", "MHPasswordManager"))
                .andExpect(model().attribute("state", "state123"))
                .andExpect(model().attribute("principalName", "testuser"))
                .andExpect(model().attributeExists("scopes"));
    }
}
