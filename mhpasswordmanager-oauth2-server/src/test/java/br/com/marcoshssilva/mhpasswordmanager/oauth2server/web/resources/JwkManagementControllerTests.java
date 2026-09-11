package br.com.marcoshssilva.mhpasswordmanager.oauth2server.web.resources;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.JwkKeyData;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.JwkKeyService;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.utils.JwksUtils;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.web.errors.ControllerAdviceResolver;
import com.nimbusds.jose.jwk.RSAKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class JwkManagementControllerTests {

    @Mock
    private JwkKeyService jwkKeyService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        JwkManagementController controller = new JwkManagementController(jwkKeyService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ControllerAdviceResolver())
                .build();
    }

    @DisplayName("Should return all JWK keys")
    @Test
    void shouldReturnAllJwkKeys() throws Exception {
        UUID uuid = UUID.randomUUID();
        JwkKeyData keyData = JwkKeyData.builder()
                .uuid(uuid.toString())
                .algorithm("RSA")
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();

        when(jwkKeyService.getAllKeys()).thenReturn(List.of(keyData));

        mockMvc.perform(get("/api/jwk-management/get/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].uuid").value(uuid.toString()))
                .andExpect(jsonPath("$.data[0].algorithm").value("RSA"));
    }

    @DisplayName("Should return single JWK key when found")
    @Test
    void shouldReturnSingleJwkKeyWhenFound() throws Exception {
        UUID uuid = UUID.randomUUID();
        JwkKeyData keyData = JwkKeyData.builder()
                .uuid(uuid.toString())
                .algorithm("RSA")
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();

        when(jwkKeyService.getJwkKey(uuid)).thenReturn(keyData);

        mockMvc.perform(get("/api/jwk-management/get/{uuid}", uuid.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.uuid").value(uuid.toString()));
    }

    @DisplayName("Should return 404 NOT_FOUND when JWK key not found")
    @Test
    void shouldReturnNotFoundWhenJwkKeyNotFound() throws Exception {
        UUID uuid = UUID.randomUUID();
        when(jwkKeyService.getJwkKey(uuid)).thenReturn(null);

        mockMvc.perform(get("/api/jwk-management/get/{uuid}", uuid.toString()))
                .andExpect(status().isNotFound());
    }

    @DisplayName("Should select JWK key successfully")
    @Test
    void shouldSelectJwkKeySuccessfully() throws Exception {
        UUID uuid = UUID.randomUUID();
        doNothing().when(jwkKeyService).selectJwkKey(uuid);

        mockMvc.perform(get("/api/jwk-management/select/{uuid}", uuid.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Jwk key selected with success"));
    }

    @DisplayName("Should delete JWK key successfully")
    @Test
    void shouldDeleteJwkKeySuccessfully() throws Exception {
        UUID uuid = UUID.randomUUID();
        doNothing().when(jwkKeyService).deleteJwkKey(uuid);

        mockMvc.perform(delete("/api/jwk-management/delete/{uuid}", uuid.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Jwk key deleted with success"));
    }

    @DisplayName("Should generate self signed JWK key successfully")
    @Test
    void shouldGenerateSelfSignedJwkKeySuccessfully() throws Exception {
        UUID uuid = UUID.randomUUID();
        when(jwkKeyService.createNotUsedUUID()).thenReturn(uuid);
        when(jwkKeyService.createJwkKey(any(JwkKeyData.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/api/jwk-management/generate-self-signed"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Jwk key created with success"))
                .andExpect(jsonPath("$.data.uuid").value(uuid.toString()))
                .andExpect(jsonPath("$.data.algorithm").value("RSA"));
    }

    @DisplayName("Should import RSA JWK key successfully via multipart")
    @Test
    void shouldImportRsaJwkKeySuccessfully() throws Exception {
        RSAKey rsaKey = JwksUtils.generateRsa();
        String pemPriv = "-----BEGIN PRIVATE KEY-----\n" + Base64.getEncoder().encodeToString(rsaKey.toPrivateKey().getEncoded()) + "\n-----END PRIVATE KEY-----";
        String pemPub = "-----BEGIN PUBLIC KEY-----\n" + Base64.getEncoder().encodeToString(rsaKey.toPublicKey().getEncoded()) + "\n-----END PUBLIC KEY-----";

        MockMultipartFile privateFile = new MockMultipartFile("private_key_file", "private.key", "application/octet-stream", pemPriv.getBytes(StandardCharsets.UTF_8));
        MockMultipartFile publicFile = new MockMultipartFile("public_key_file", "public.key", "application/octet-stream", pemPub.getBytes(StandardCharsets.UTF_8));

        UUID uuid = UUID.randomUUID();
        when(jwkKeyService.createNotUsedUUID()).thenReturn(uuid);
        when(jwkKeyService.createJwkKey(any(JwkKeyData.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(multipart("/api/jwk-management/import/rsa")
                        .file(privateFile)
                        .file(publicFile))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Jwk key created with success"))
                .andExpect(jsonPath("$.data.uuid").value(uuid.toString()))
                .andExpect(jsonPath("$.data.algorithm").value("RSA"));
    }
}
