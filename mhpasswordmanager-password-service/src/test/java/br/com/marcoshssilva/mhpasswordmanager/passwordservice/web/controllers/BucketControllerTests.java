package br.com.marcoshssilva.mhpasswordmanager.passwordservice.web.controllers;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.UserBucketService;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.exceptions.BucketNotFoundException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.models.BucketDataModel;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.models.BucketNewDataModel;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.models.BucketUpdateDataModel;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.common.impl.ResultDataFactoryImpl;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.UserAuthorizations;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.UserRegistrationService;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.exceptions.UserAuthorizationCannotBeLoadedException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.models.UserAuthorizationModel;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.web.data.requests.PasswordBucketControllerCreateBucketRequestBody;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.web.data.requests.PasswordBucketControllerUpdateBucketRequestBody;
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
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BucketControllerTests {

    @Mock
    private UserRegistrationService userRegistrationService;

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
        BucketController controller = new BucketController(userRegistrationService, userBucketService);
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

    @DisplayName("Should create bucket and return 200 OK")
    @Test
    void shouldCreateBucket() throws Exception {
        when(userRegistrationService.getUserAuthorizations(mockJwt)).thenReturn(userAuth);

        BucketDataModel bucketData = BucketDataModel.builder()
                .bucketUuid("bucket-1")
                .bucketName("My Bucket")
                .bucketDescription("Desc")
                .createdAt(LocalDateTime.now())
                .lastUpdate(LocalDateTime.now())
                .build();

        when(userBucketService.createBucket(any(BucketNewDataModel.class), eq(userAuth)))
                .thenReturn(new ResultDataFactoryImpl<BucketDataModel>().success(bucketData, "CREATED"));

        PasswordBucketControllerCreateBucketRequestBody requestBody = new PasswordBucketControllerCreateBucketRequestBody();
        requestBody.setBucketName("My Bucket");
        requestBody.setBucketDescription("Desc");
        requestBody.setBucketSecret("secret123");

        mockMvc.perform(MockMvcRequestBuilders.post("/bucket/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bucketUuid").value("bucket-1"))
                .andExpect(jsonPath("$.bucketName").value("My Bucket"))
                .andExpect(jsonPath("$.bucketDescription").value("Desc"));
    }

    @DisplayName("Should return 500 when UserAuthorizationCannotBeLoadedException is thrown in createBucket")
    @Test
    void shouldHandleAuthExceptionInCreateBucket() throws Exception {
        when(userRegistrationService.getUserAuthorizations(mockJwt)).thenThrow(new UserAuthorizationCannotBeLoadedException("Auth failed"));

        PasswordBucketControllerCreateBucketRequestBody requestBody = new PasswordBucketControllerCreateBucketRequestBody();
        requestBody.setBucketName("My Bucket");

        mockMvc.perform(MockMvcRequestBuilders.post("/bucket/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Auth failed"));
    }

    @DisplayName("Should get all buckets with pagination")
    @Test
    void shouldGetAllBuckets() throws Exception {
        when(userRegistrationService.getUserAuthorizations(mockJwt)).thenReturn(userAuth);

        BucketDataModel bucketData = BucketDataModel.builder()
                .bucketUuid("bucket-1")
                .bucketName("My Bucket")
                .build();

        when(userBucketService.getBucketsByUserAuthorizations(eq(userAuth), any(Pageable.class)))
                .thenReturn(new ResultDataFactoryImpl<org.springframework.data.domain.Page<BucketDataModel>>()
                        .success(new PageImpl<>(List.of(bucketData)), "SUCCESS"));

        mockMvc.perform(MockMvcRequestBuilders.get("/bucket"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].bucketUuid").value("bucket-1"))
                .andExpect(jsonPath("$.content[0].bucketName").value("My Bucket"));
    }

    @DisplayName("Should get bucket by uuid")
    @Test
    void shouldGetBucketByUuid() throws Exception {
        when(userRegistrationService.getUserAuthorizations(mockJwt)).thenReturn(userAuth);

        BucketDataModel bucketData = BucketDataModel.builder()
                .bucketUuid("bucket-1")
                .bucketName("My Bucket")
                .build();

        when(userBucketService.getBucketByUuid("bucket-1", userAuth))
                .thenReturn(new ResultDataFactoryImpl<BucketDataModel>().success(bucketData, "SUCCESS"));

        mockMvc.perform(MockMvcRequestBuilders.get("/bucket/bucket-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bucketUuid").value("bucket-1"))
                .andExpect(jsonPath("$.bucketName").value("My Bucket"));
    }

    @DisplayName("Should return 404 when bucket is not found by uuid")
    @Test
    void shouldReturnNotFoundWhenBucketNotFound() throws Exception {
        when(userRegistrationService.getUserAuthorizations(mockJwt)).thenReturn(userAuth);

        when(userBucketService.getBucketByUuid("not-found", userAuth))
                .thenReturn(new ResultDataFactoryImpl<BucketDataModel>().exception(new BucketNotFoundException("Bucket not found"), "Bucket not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/bucket/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Bucket not found"));
    }

    @DisplayName("Should delete bucket by uuid")
    @Test
    void shouldDeleteBucket() throws Exception {
        when(userRegistrationService.getUserAuthorizations(mockJwt)).thenReturn(userAuth);

        when(userBucketService.deleteBucketByUuid("bucket-1", userAuth))
                .thenReturn(new ResultDataFactoryImpl<Boolean>().success(Boolean.TRUE, "DELETED"));

        mockMvc.perform(MockMvcRequestBuilders.delete("/bucket/bucket-1"))
                .andExpect(status().isOk());
    }

    @DisplayName("Should update bucket by uuid")
    @Test
    void shouldUpdateBucket() throws Exception {
        when(userRegistrationService.getUserAuthorizations(mockJwt)).thenReturn(userAuth);

        BucketDataModel updatedBucket = BucketDataModel.builder()
                .bucketUuid("bucket-1")
                .bucketName("Updated Bucket")
                .bucketDescription("Updated Desc")
                .build();

        when(userBucketService.updateBucket(eq("bucket-1"), any(BucketUpdateDataModel.class), eq(userAuth)))
                .thenReturn(new ResultDataFactoryImpl<BucketDataModel>().success(updatedBucket, "UPDATED"));

        PasswordBucketControllerUpdateBucketRequestBody requestBody = new PasswordBucketControllerUpdateBucketRequestBody();
        requestBody.setBucketName("Updated Bucket");
        requestBody.setBucketDescription("Updated Desc");

        mockMvc.perform(MockMvcRequestBuilders.put("/bucket/bucket-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bucketUuid").value("bucket-1"))
                .andExpect(jsonPath("$.bucketName").value("Updated Bucket"));
    }
}
