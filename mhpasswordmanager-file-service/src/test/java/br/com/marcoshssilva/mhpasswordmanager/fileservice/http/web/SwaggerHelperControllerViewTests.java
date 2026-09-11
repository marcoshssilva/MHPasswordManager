package br.com.marcoshssilva.mhpasswordmanager.fileservice.http.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

class SwaggerHelperControllerViewTests {

    @DisplayName("Should return oauth2-redirect view on /swagger-ui/redirect")
    @Test
    void shouldReturnOAuth2RedirectView() throws Exception {
        SwaggerHelperControllerView controller = new SwaggerHelperControllerView();
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        mockMvc.perform(MockMvcRequestBuilders.get("/swagger-ui/redirect"))
                .andExpect(status().isOk())
                .andExpect(view().name("oauth2-redirect"));
    }
}
