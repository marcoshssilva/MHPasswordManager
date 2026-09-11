package br.com.marcoshssilva.mhpasswordmanager.passwordservice.web.views;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

class SwaggerHelperControllerViewTests {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new SwaggerHelperControllerView()).build();
    }

    @DisplayName("Should return redirect view name for swagger oauth2 redirect")
    @Test
    void shouldReturnOAuth2SwaggerRedirectUri() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/swagger-ui/redirect"))
                .andExpect(status().isOk())
                .andExpect(view().name("oauth2-redirect"));
    }
}
