package br.com.marcoshssilva.mhpasswordmanager.passwordservice.web.controllers;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.enums.PasswordKeyTypesEnum;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.UserBucketService;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.models.BucketDataModel;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.common.impl.ResultDataFactoryImpl;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.UserKeysService;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.UserStoredKeysService;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.exceptions.KeyNotFoundException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.AbstractKeyPayloadDecodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.KeyPayloadEncodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.KeyStorePayloadEncodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.application.ApplicationPasswordStoredValueDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.application.ApplicationPayloadDecodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.email.EmailSecurityQuestionStoredValueDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.UserAuthorizations;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.UserRegistrationService;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.models.UserAuthorizationModel;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.web.data.requests.SimpleBucketCryptKeyRequest;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.web.error.RestControllerExceptionManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ManageKeysControllerTests {

    @Mock
    private UserRegistrationService userRegistrationService;

    @Mock
    private UserKeysService userKeysService;

    @Mock
    private UserStoredKeysService userStoredKeysService;

    @Mock
    private UserBucketService userBucketService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

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
        objectMapper = new ObjectMapper();
        ManageKeysController controller = new ManageKeysController(
                userRegistrationService,
                userKeysService,
                userStoredKeysService,
                userBucketService
        );

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new RestControllerExceptionManager())
                .setCustomArgumentResolvers(
                        new PageableHandlerMethodArgumentResolver(),
                        new HandlerMethodArgumentResolver() {
                            @Override
                            public boolean supportsParameter(MethodParameter parameter) {
                                return parameter.getParameterType().isAssignableFrom(Jwt.class);
                            }

                            @Override
                            public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                                return mockJwt;
                            }
                        }
                )
                .build();
    }

    @DisplayName("Should get all keys in bucket")
    @Test
    void shouldGetAllKeys() throws Exception {
        when(userRegistrationService.getUserAuthorizations(mockJwt)).thenReturn(userAuth);

        KeyPayloadEncodedDto dto = KeyPayloadEncodedDto.builder().id(1L).ownerId("bucket-1").build();
        when(userKeysService.getAllEncodedKeyFromBucket(eq(userAuth), eq("bucket-1"), any(Pageable.class)))
                .thenReturn(new ResultDataFactoryImpl<org.springframework.data.domain.Page<KeyPayloadEncodedDto>>()
                        .success(new PageImpl<>(List.of(dto)), "OK"));

        mockMvc.perform(MockMvcRequestBuilders.get("/keys/bucket-1/key/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1));
    }

    @DisplayName("Should get specific key in bucket")
    @Test
    void shouldGetKey() throws Exception {
        when(userRegistrationService.getUserAuthorizations(mockJwt)).thenReturn(userAuth);

        KeyPayloadEncodedDto dto = KeyPayloadEncodedDto.builder().id(1L).ownerId("bucket-1").build();
        when(userKeysService.getEncodedKeyFromBucket(userAuth, "bucket-1", 1L))
                .thenReturn(new ResultDataFactoryImpl<KeyPayloadEncodedDto>().success(dto, "OK"));

        mockMvc.perform(MockMvcRequestBuilders.get("/keys/bucket-1/key/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @DisplayName("Should create new key in bucket")
    @Test
    void shouldSaveKey() throws Exception {
        when(userRegistrationService.getUserAuthorizations(mockJwt)).thenReturn(userAuth);

        BucketDataModel bucketData = BucketDataModel.builder().bucketUuid("bucket-1").bucketPublicKey("pubKey").build();
        when(userBucketService.getBucketByUuid("bucket-1", userAuth))
                .thenReturn(new ResultDataFactoryImpl<BucketDataModel>().success(bucketData, "OK"));

        KeyPayloadEncodedDto transformedDto = KeyPayloadEncodedDto.builder()
                .ownerId("bucket-1")
                .encodedKeys(new KeyStorePayloadEncodedDto[]{
                        KeyStorePayloadEncodedDto.builder().data("data").build()
                })
                .build();
        when(userKeysService.transformAsKeyPayloadEncodedDto(any(AbstractKeyPayloadDecodedDto.class), eq("pubKey")))
                .thenReturn(new ResultDataFactoryImpl<KeyPayloadEncodedDto>().success(transformedDto, "OK"));

        KeyPayloadEncodedDto savedDto = KeyPayloadEncodedDto.builder().id(10L).ownerId("bucket-1").build();
        when(userKeysService.saveKeyPayloadEncodedDto(eq(userAuth), any(KeyPayloadEncodedDto.class)))
                .thenReturn(new ResultDataFactoryImpl<KeyPayloadEncodedDto>().success(savedDto, "OK"));

        ApplicationPayloadDecodedDto payload = ApplicationPayloadDecodedDto.builder().appName("App").build();

        mockMvc.perform(MockMvcRequestBuilders.post("/keys/bucket-1/key/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10));
    }

    @DisplayName("Should update key in bucket")
    @Test
    void shouldUpdateKey() throws Exception {
        when(userRegistrationService.getUserAuthorizations(mockJwt)).thenReturn(userAuth);

        BucketDataModel bucketData = BucketDataModel.builder().bucketUuid("bucket-1").bucketPublicKey("pubKey").build();
        when(userBucketService.getBucketByUuid("bucket-1", userAuth))
                .thenReturn(new ResultDataFactoryImpl<BucketDataModel>().success(bucketData, "OK"));

        KeyPayloadEncodedDto transformedDto = KeyPayloadEncodedDto.builder().ownerId("bucket-1").build();
        when(userKeysService.transformAsKeyPayloadEncodedDto(any(AbstractKeyPayloadDecodedDto.class), eq("pubKey")))
                .thenReturn(new ResultDataFactoryImpl<KeyPayloadEncodedDto>().success(transformedDto, "OK"));

        KeyPayloadEncodedDto updatedDto = KeyPayloadEncodedDto.builder().id(1L).ownerId("bucket-1").build();
        when(userKeysService.updateKeyPayloadEncodedDto(eq(userAuth), any(KeyPayloadEncodedDto.class)))
                .thenReturn(new ResultDataFactoryImpl<KeyPayloadEncodedDto>().success(updatedDto, "UPDATED"));

        ApplicationPayloadDecodedDto payload = ApplicationPayloadDecodedDto.builder().appName("App").build();

        mockMvc.perform(MockMvcRequestBuilders.put("/keys/bucket-1/key/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @DisplayName("Should delete key in bucket")
    @Test
    void shouldDeleteKey() throws Exception {
        when(userRegistrationService.getUserAuthorizations(mockJwt)).thenReturn(userAuth);

        when(userKeysService.deleteKeyPayload(userAuth, "bucket-1", 1L))
                .thenReturn(new ResultDataFactoryImpl<Void>().success(null, "OK"));

        mockMvc.perform(MockMvcRequestBuilders.delete("/keys/bucket-1/key/1"))
                .andExpect(status().isOk());
    }

    @DisplayName("Should get key stored payload")
    @Test
    void shouldGetKeyPayload() throws Exception {
        when(userRegistrationService.getUserAuthorizations(mockJwt)).thenReturn(userAuth);

        KeyStorePayloadEncodedDto dto = KeyStorePayloadEncodedDto.builder().id(100L).data("data").build();
        when(userStoredKeysService.getStoredKey(userAuth, "bucket-1", 1L, 100L))
                .thenReturn(new ResultDataFactoryImpl<KeyStorePayloadEncodedDto>().success(dto, "OK"));

        mockMvc.perform(MockMvcRequestBuilders.get("/keys/bucket-1/key/1/stored/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100));
    }

    @DisplayName("Should create password stored key")
    @Test
    void shouldSaveKeyPayloadPassword() throws Exception {
        when(userRegistrationService.getUserAuthorizations(mockJwt)).thenReturn(userAuth);

        KeyStorePayloadEncodedDto dto = KeyStorePayloadEncodedDto.builder().id(100L).data("data").build();
        when(userStoredKeysService.createPasswordStoredKey(eq(userAuth), eq("bucket-1"), eq(1L), any(ApplicationPasswordStoredValueDto.class)))
                .thenReturn(new ResultDataFactoryImpl<KeyStorePayloadEncodedDto>().success(dto, "OK"));

        ApplicationPasswordStoredValueDto body = ApplicationPasswordStoredValueDto.builder().password("pass").build();

        mockMvc.perform(MockMvcRequestBuilders.post("/keys/bucket-1/key/1/stored/new/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100));
    }

    @DisplayName("Should update password stored key")
    @Test
    void shouldUpdateKeyPayloadPassword() throws Exception {
        when(userRegistrationService.getUserAuthorizations(mockJwt)).thenReturn(userAuth);

        KeyStorePayloadEncodedDto dto = KeyStorePayloadEncodedDto.builder().id(100L).data("data").build();
        when(userStoredKeysService.savePasswordStoredKey(eq(userAuth), eq("bucket-1"), eq(1L), eq(100L), any(ApplicationPasswordStoredValueDto.class)))
                .thenReturn(new ResultDataFactoryImpl<KeyStorePayloadEncodedDto>().success(dto, "OK"));

        ApplicationPasswordStoredValueDto body = ApplicationPasswordStoredValueDto.builder().password("pass").build();

        mockMvc.perform(MockMvcRequestBuilders.put("/keys/bucket-1/key/1/stored/100/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100));
    }

    @DisplayName("Should create security question stored key")
    @Test
    void shouldSaveKeyPayloadSecurityQuestion() throws Exception {
        when(userRegistrationService.getUserAuthorizations(mockJwt)).thenReturn(userAuth);

        KeyStorePayloadEncodedDto dto = KeyStorePayloadEncodedDto.builder().id(200L).data("data").build();
        when(userStoredKeysService.createSecurityQuestionStoredKey(eq(userAuth), eq("bucket-1"), eq(1L), any(EmailSecurityQuestionStoredValueDto.class)))
                .thenReturn(new ResultDataFactoryImpl<KeyStorePayloadEncodedDto>().success(dto, "OK"));

        EmailSecurityQuestionStoredValueDto body = EmailSecurityQuestionStoredValueDto.builder().question("Q").build();

        mockMvc.perform(MockMvcRequestBuilders.post("/keys/bucket-1/key/1/stored/new/security-question")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(200));
    }

    @DisplayName("Should update security question stored key")
    @Test
    void shouldUpdateKeyPayloadSecurityQuestion() throws Exception {
        when(userRegistrationService.getUserAuthorizations(mockJwt)).thenReturn(userAuth);

        KeyStorePayloadEncodedDto dto = KeyStorePayloadEncodedDto.builder().id(200L).data("data").build();
        when(userStoredKeysService.saveStoredQuestionStoredKey(eq(userAuth), eq("bucket-1"), eq(1L), eq(200L), any(EmailSecurityQuestionStoredValueDto.class)))
                .thenReturn(new ResultDataFactoryImpl<KeyStorePayloadEncodedDto>().success(dto, "OK"));

        EmailSecurityQuestionStoredValueDto body = EmailSecurityQuestionStoredValueDto.builder().question("Q").build();

        mockMvc.perform(MockMvcRequestBuilders.put("/keys/bucket-1/key/1/stored/200/security-question")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(200));
    }

    @DisplayName("Should delete key payload")
    @Test
    void shouldDeleteKeyPayload() throws Exception {
        when(userRegistrationService.getUserAuthorizations(mockJwt)).thenReturn(userAuth);

        when(userStoredKeysService.deleteStoredKey(userAuth, "bucket-1", 1L, 100L))
                .thenReturn(new ResultDataFactoryImpl<Void>().success(null, "OK"));

        mockMvc.perform(MockMvcRequestBuilders.delete("/keys/bucket-1/key/1/stored/100"))
                .andExpect(status().isOk());
    }

    @DisplayName("Should encrypt base64 using bucket")
    @Test
    void shouldEncryptBase64() throws Exception {
        when(userRegistrationService.getUserAuthorizations(mockJwt)).thenReturn(userAuth);

        when(userStoredKeysService.encryptBase64UsingBucket(userAuth, "bucket-1", "plainBase64"))
                .thenReturn(new ResultDataFactoryImpl<String>().success("encBase64", "OK"));

        SimpleBucketCryptKeyRequest body = new SimpleBucketCryptKeyRequest();
        body.setBase64Data("plainBase64");

        mockMvc.perform(MockMvcRequestBuilders.post("/keys/bucket-1/encrypt/base64")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("encBase64"));
    }

    @DisplayName("Should encrypt file using bucket")
    @Test
    void shouldEncryptFile() throws Exception {
        when(userRegistrationService.getUserAuthorizations(mockJwt)).thenReturn(userAuth);

        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "hello".getBytes());
        String encodedContent = java.util.Base64.getEncoder().encodeToString("hello".getBytes());

        when(userStoredKeysService.encryptBase64UsingBucket(userAuth, "bucket-1", encodedContent))
                .thenReturn(new ResultDataFactoryImpl<String>().success("fileEncBase64", "OK"));

        mockMvc.perform(MockMvcRequestBuilders.multipart("/keys/bucket-1/encrypt/file")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("fileEncBase64"));
    }
}
