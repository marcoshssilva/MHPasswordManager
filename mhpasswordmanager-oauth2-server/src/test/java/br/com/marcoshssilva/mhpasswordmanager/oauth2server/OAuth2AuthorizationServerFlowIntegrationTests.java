package br.com.marcoshssilva.mhpasswordmanager.oauth2server;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = MhPasswordManagerOAuth2AuthorizationServerApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles({"test"})
@Import(RabbitMQMockTestConfiguration.class)
class OAuth2AuthorizationServerFlowIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @DisplayName("Should expose JWK set endpoint")
    @Test
    void shouldExposeJwkSetEndpoint() throws Exception {
        mockMvc.perform(get("/oauth2/jwks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.keys").isArray())
                .andExpect(jsonPath("$.keys[0].kty").value("RSA"));
    }

    @DisplayName("Should expose OpenID configuration endpoint")
    @Test
    void shouldExposeOpenIdConfigurationEndpoint() throws Exception {
        mockMvc.perform(get("/.well-known/openid-configuration"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.issuer").exists())
                .andExpect(jsonPath("$.token_endpoint").exists())
                .andExpect(jsonPath("$.jwks_uri").exists());
    }

    @DisplayName("Should issue token via Client Credentials grant")
    @Test
    void shouldIssueTokenViaClientCredentials() throws Exception {
        mockMvc.perform(post("/oauth2/token")
                        .with(httpBasic("MHPasswordManager-GlobalAdmin", "9f45dc98-8e4f-11ee-b9d1-0242ac120002"))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("grant_type", "client_credentials")
                        .param("scope", "global:fullAccess"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").isString())
                .andExpect(jsonPath("$.token_type").value("Bearer"))
                .andExpect(jsonPath("$.expires_in").isNumber());
    }

    @DisplayName("Should reject token request with invalid client credentials")
    @Test
    void shouldRejectInvalidClientCredentials() throws Exception {
        mockMvc.perform(post("/oauth2/token")
                        .with(httpBasic("MHPasswordManager-GlobalAdmin", "wrong-secret"))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("grant_type", "client_credentials"))
                .andExpect(status().isUnauthorized());
    }
}
