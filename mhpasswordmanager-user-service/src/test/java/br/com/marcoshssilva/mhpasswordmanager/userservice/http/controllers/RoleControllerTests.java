package br.com.marcoshssilva.mhpasswordmanager.userservice.http.controllers;

import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.enums.DefaultUserRoles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RoleControllerTests {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        RoleController controller = new RoleController();
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @DisplayName("Should return all default user roles")
    @Test
    void shouldReturnAllDefaultUserRoles() throws Exception {
        mockMvc.perform(get("/role/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(DefaultUserRoles.values().length));
    }
}
