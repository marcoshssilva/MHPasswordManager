package br.com.marcoshssilva.mhpasswordmanager.passwordservice.web.controllers;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt.CryptService;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.UserBucketService;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.models.BucketDataModel;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.common.impl.ResultDataFactoryImpl;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.UserAuthorizations;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.UserRegistrationService;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.models.UserAuthorizationModel;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.web.data.requests.AesCryptKeyRequest;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.web.data.requests.RsaCryptKeyRequest;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.web.error.RestControllerExceptionManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.time.Instant;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CryptKeyControllerTests {

    @Mock
    private CryptService cryptAesService;

    @Mock
    private CryptService cryptRsaService;

    @Mock
    private UserRegistrationService userRegistrationService;

    @Mock
    private UserBucketService userBucketService;

    private ObjectMapper mapper;
    private MockMvc mockMvc;

    private final Jwt mockJwt = new Jwt(
            "token",
            Instant.now(),
            Instant.now().plusSeconds(3600),
            Map.of("alg", "none"),
            Map.of("sub", "john")
    );

    private final UserAuthorizations userAuth = UserAuthorizationModel.builder()
            .username("john")
            .roles(Collections.emptySet())
            .profiles(Collections.emptySet())
            .build();

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        CryptKeyController controller = new CryptKeyController(
                cryptAesService,
                cryptRsaService,
                userRegistrationService,
                mapper,
                userBucketService
        );

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

    @DisplayName("Should decrypt RSA data and return as Base64")
    @Test
    void shouldDecryptRsaDataAsBase64() throws Exception {
        when(userRegistrationService.getUserAuthorizations(mockJwt)).thenReturn(userAuth);

        String encryptedPrivKey = Base64.getEncoder().encodeToString("encrypted-priv-key".getBytes());
        BucketDataModel bucketData = BucketDataModel.builder()
                .bucketUuid("bucket-1")
                .bucketPrivateKeyEncrypted(encryptedPrivKey)
                .build();
        when(userBucketService.getBucketByUuid("bucket-1", userAuth))
                .thenReturn(new ResultDataFactoryImpl<BucketDataModel>().success(bucketData, "OK"));

        when(cryptAesService.decrypt(any(), eq("bucketSecret"))).thenReturn("decrypted-priv-key".getBytes());
        when(cryptRsaService.convertByteToBase64("decrypted-priv-key".getBytes())).thenReturn("privKeyBase64");
        when(cryptRsaService.decrypt(any(), eq("privKeyBase64"))).thenReturn("decryptedPayload".getBytes());

        RsaCryptKeyRequest payload = new RsaCryptKeyRequest();
        payload.setBucketUuid("bucket-1");
        payload.setSecret("bucketSecret");
        payload.setBase64Data(Base64.getEncoder().encodeToString("input".getBytes()));

        mockMvc.perform(MockMvcRequestBuilders.post("/crypt/rsa/decrypt/base64")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(Base64.getEncoder().encodeToString("decryptedPayload".getBytes())));
    }

    @DisplayName("Should decrypt RSA data and return as JSON")
    @Test
    void shouldDecryptRsaDataAsJson() throws Exception {
        when(userRegistrationService.getUserAuthorizations(mockJwt)).thenReturn(userAuth);

        String encryptedPrivKey = Base64.getEncoder().encodeToString("encrypted-priv-key".getBytes());
        BucketDataModel bucketData = BucketDataModel.builder()
                .bucketUuid("bucket-1")
                .bucketPrivateKeyEncrypted(encryptedPrivKey)
                .build();
        when(userBucketService.getBucketByUuid("bucket-1", userAuth))
                .thenReturn(new ResultDataFactoryImpl<BucketDataModel>().success(bucketData, "OK"));

        when(cryptAesService.decrypt(any(), eq("bucketSecret"))).thenReturn("decrypted-priv-key".getBytes());
        when(cryptRsaService.convertByteToBase64("decrypted-priv-key".getBytes())).thenReturn("privKeyBase64");
        when(cryptRsaService.decrypt(any(), eq("privKeyBase64"))).thenReturn("{\"key\":\"value\"}".getBytes());
        when(cryptRsaService.convertByteToString("{\"key\":\"value\"}".getBytes())).thenReturn("{\"key\":\"value\"}");

        RsaCryptKeyRequest payload = new RsaCryptKeyRequest();
        payload.setBucketUuid("bucket-1");
        payload.setSecret("bucketSecret");
        payload.setBase64Data(Base64.getEncoder().encodeToString("input".getBytes()));

        mockMvc.perform(MockMvcRequestBuilders.post("/crypt/rsa/decrypt/json")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key").value("value"));
    }

    @DisplayName("Should decrypt AES data and return as Base64")
    @Test
    void shouldDecryptAesDataAsBase64() throws Exception {
        when(cryptAesService.decrypt(any(), eq("aesSecret"))).thenReturn("aesDecrypted".getBytes());

        AesCryptKeyRequest payload = new AesCryptKeyRequest();
        payload.setSecret("aesSecret");
        payload.setBase64Data(Base64.getEncoder().encodeToString("cipher".getBytes()));

        mockMvc.perform(MockMvcRequestBuilders.post("/crypt/aes/decrypt/base64")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(Base64.getEncoder().encodeToString("aesDecrypted".getBytes())));
    }

    @DisplayName("Should decrypt AES data and return as JSON")
    @Test
    void shouldDecryptAesDataAsJson() throws Exception {
        when(cryptAesService.decrypt(any(), eq("aesSecret"))).thenReturn("{\"foo\":\"bar\"}".getBytes());
        when(cryptRsaService.convertByteToString("{\"foo\":\"bar\"}".getBytes())).thenReturn("{\"foo\":\"bar\"}");

        AesCryptKeyRequest payload = new AesCryptKeyRequest();
        payload.setSecret("aesSecret");
        payload.setBase64Data(Base64.getEncoder().encodeToString("cipher".getBytes()));

        mockMvc.perform(MockMvcRequestBuilders.post("/crypt/aes/decrypt/json")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.foo").value("bar"));
    }

    @DisplayName("Should encrypt data in AES and return Base64 string")
    @Test
    void shouldEncryptAesDataAsBase64() throws Exception {
        when(cryptAesService.encrypt(any(), eq("aesSecret"))).thenReturn("encryptedAesBytes".getBytes());
        when(cryptAesService.convertByteToBase64("encryptedAesBytes".getBytes())).thenReturn("aesEncBase64");

        AesCryptKeyRequest payload = new AesCryptKeyRequest();
        payload.setSecret("aesSecret");
        payload.setBase64Data(Base64.getEncoder().encodeToString("plain".getBytes()));

        mockMvc.perform(MockMvcRequestBuilders.post("/crypt/aes/encrypt/base64")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(content().string("aesEncBase64"));
    }

    @DisplayName("Should encrypt data in RSA and return Base64 string")
    @Test
    void shouldEncryptRsaDataAsBase64() throws Exception {
        when(userRegistrationService.getUserAuthorizations(mockJwt)).thenReturn(userAuth);

        BucketDataModel bucketData = BucketDataModel.builder()
                .bucketUuid("bucket-1")
                .bucketPublicKey("pubKeyBase64")
                .build();
        when(userBucketService.getBucketByUuid("bucket-1", userAuth))
                .thenReturn(new ResultDataFactoryImpl<BucketDataModel>().success(bucketData, "OK"));

        when(cryptRsaService.encrypt(any(), eq("pubKeyBase64"))).thenReturn("rsaEncrypted".getBytes());

        RsaCryptKeyRequest payload = new RsaCryptKeyRequest();
        payload.setBucketUuid("bucket-1");
        payload.setBase64Data(Base64.getEncoder().encodeToString("plain".getBytes()));

        mockMvc.perform(MockMvcRequestBuilders.post("/crypt/rsa/encrypt/base64")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(content().string(Base64.getEncoder().encodeToString("rsaEncrypted".getBytes())));
    }
}
