package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.impl;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities.UserBucket;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities.UserPasswordKey;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities.UserPasswordKeyType;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities.UserPasswordStoredValue;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.enums.PasswordKeyTypesEnum;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.repositories.UserPasswordKeyRepository;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.repositories.UserPasswordStoredValueRepository;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt.CryptService;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.UserBucketService;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.models.BucketDataModel;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.common.IResultData;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.common.impl.ResultDataFactoryImpl;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.exceptions.KeyNotFoundException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.exceptions.KeyRegistrationErrorException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.KeyStorePayloadEncodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.application.ApplicationPasswordStoredValueDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.email.EmailPasswordStoredValueDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.email.EmailSecurityQuestionStoredValueDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.social.SocialMediaPasswordStoredValueDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.social.SocialMediaSecurityQuestionStoredValueDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.website.WebsitePasswordStoredValueDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.UserAuthorizations;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.models.UserAuthorizationModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserStoredKeysServiceImplTests {

    @Mock
    private CryptService cryptService;

    @Mock
    private UserPasswordKeyRepository userPasswordKeyRepository;

    @Mock
    private UserPasswordStoredValueRepository userPasswordStoredValueRepository;

    @Mock
    private UserBucketService userBucketService;

    private ObjectMapper objectMapper;
    private UserStoredKeysServiceImpl service;
    private UserAuthorizations userAuth;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        service = new UserStoredKeysServiceImpl(
                cryptService,
                userPasswordKeyRepository,
                userPasswordStoredValueRepository,
                userBucketService,
                objectMapper
        );
        userAuth = UserAuthorizationModel.builder().username("user1").roles(Collections.emptySet()).profiles(Collections.emptySet()).build();
    }

    @DisplayName("Should create password stored key successfully")
    @Test
    void shouldCreatePasswordStoredKeySuccessfully() {
        BucketDataModel bucketData = BucketDataModel.builder().bucketUuid("bucket-1").bucketPublicKey("pub-key").build();
        when(userBucketService.getBucketByUuid("bucket-1", userAuth)).thenReturn(new ResultDataFactoryImpl<BucketDataModel>().success(bucketData, "OK"));

        UserBucket bucket = UserBucket.builder().id("bucket-1").build();
        UserPasswordKey key = UserPasswordKey.builder()
                .id(1L)
                .userBucket(bucket)
                .type(UserPasswordKeyType.builder().id(4L).description("APPLICATION").build())
                .build();
        when(userPasswordKeyRepository.findByBucketUuidAndId("bucket-1", 1L)).thenReturn(Optional.of(key));

        when(cryptService.encrypt(any(), eq("pub-key"))).thenReturn("enc".getBytes());
        when(cryptService.convertByteToBase64("enc".getBytes())).thenReturn("encBase64");

        UserPasswordStoredValue savedVal = UserPasswordStoredValue.builder()
                .id(10L)
                .data("encBase64")
                .createdAt(LocalDateTime.now())
                .lastUpdate(LocalDateTime.now())
                .build();
        when(userPasswordStoredValueRepository.save(any(UserPasswordStoredValue.class))).thenReturn(savedVal);

        ApplicationPasswordStoredValueDto dto = ApplicationPasswordStoredValueDto.builder()
                .password("secret")
                .appName("App")
                .build();

        IResultData<KeyStorePayloadEncodedDto> result = service.createPasswordStoredKey(userAuth, "bucket-1", 1L, dto);

        assertThat(result.isOk()).isTrue();
        assertThat(result.getData().getId()).isEqualTo(10L);
        assertThat(result.getData().getData()).isEqualTo("encBase64");
    }

    @DisplayName("Should return error on createPasswordStoredKey when bucket fails")
    @Test
    void shouldReturnErrorWhenBucketFailsOnCreatePassword() {
        when(userBucketService.getBucketByUuid("bucket-1", userAuth)).thenReturn(new ResultDataFactoryImpl<BucketDataModel>().error("Bucket not found"));

        ApplicationPasswordStoredValueDto dto = ApplicationPasswordStoredValueDto.builder().build();
        IResultData<KeyStorePayloadEncodedDto> result = service.createPasswordStoredKey(userAuth, "bucket-1", 1L, dto);

        assertThat(result.hasError()).isTrue();
    }

    @DisplayName("Should return error on createPasswordStoredKey when type does not match")
    @Test
    void shouldReturnErrorWhenTypeMismatchOnCreatePassword() {
        BucketDataModel bucketData = BucketDataModel.builder().bucketUuid("bucket-1").bucketPublicKey("pub-key").build();
        when(userBucketService.getBucketByUuid("bucket-1", userAuth)).thenReturn(new ResultDataFactoryImpl<BucketDataModel>().success(bucketData, "OK"));

        UserBucket bucket = UserBucket.builder().id("bucket-1").build();
        UserPasswordKey key = UserPasswordKey.builder()
                .id(1L)
                .userBucket(bucket)
                .type(UserPasswordKeyType.builder().id(1L).description("EMAILS").build())
                .build();
        when(userPasswordKeyRepository.findByBucketUuidAndId("bucket-1", 1L)).thenReturn(Optional.of(key));

        ApplicationPasswordStoredValueDto dto = ApplicationPasswordStoredValueDto.builder().build();
        IResultData<KeyStorePayloadEncodedDto> result = service.createPasswordStoredKey(userAuth, "bucket-1", 1L, dto);

        assertThat(result.hasError()).isTrue();
        assertThat(result.getException()).isInstanceOf(KeyRegistrationErrorException.class);
    }

    @DisplayName("Should create security question stored key successfully")
    @Test
    void shouldCreateSecurityQuestionStoredKeySuccessfully() {
        BucketDataModel bucketData = BucketDataModel.builder().bucketUuid("bucket-1").bucketPublicKey("pub-key").build();
        when(userBucketService.getBucketByUuid("bucket-1", userAuth)).thenReturn(new ResultDataFactoryImpl<BucketDataModel>().success(bucketData, "OK"));

        UserBucket bucket = UserBucket.builder().id("bucket-1").build();
        UserPasswordKey key = UserPasswordKey.builder()
                .id(1L)
                .userBucket(bucket)
                .type(UserPasswordKeyType.builder().id(1L).description("EMAILS").build())
                .build();
        when(userPasswordKeyRepository.findByBucketUuidAndId("bucket-1", 1L)).thenReturn(Optional.of(key));

        when(cryptService.encrypt(any(), eq("pub-key"))).thenReturn("enc".getBytes());
        when(cryptService.convertByteToBase64("enc".getBytes())).thenReturn("encBase64");

        UserPasswordStoredValue savedVal = UserPasswordStoredValue.builder()
                .id(20L)
                .data("encBase64")
                .createdAt(LocalDateTime.now())
                .lastUpdate(LocalDateTime.now())
                .build();
        when(userPasswordStoredValueRepository.save(any(UserPasswordStoredValue.class))).thenReturn(savedVal);

        EmailSecurityQuestionStoredValueDto dto = EmailSecurityQuestionStoredValueDto.builder()
                .question("Q?")
                .expectedValue("A")
                .build();

        IResultData<KeyStorePayloadEncodedDto> result = service.createSecurityQuestionStoredKey(userAuth, "bucket-1", 1L, dto);

        assertThat(result.isOk()).isTrue();
        assertThat(result.getData().getId()).isEqualTo(20L);
    }

    @DisplayName("Should return error on createSecurityQuestionStoredKey when type mismatch")
    @Test
    void shouldReturnErrorWhenTypeMismatchOnCreateSecurityQuestion() {
        BucketDataModel bucketData = BucketDataModel.builder().bucketUuid("bucket-1").bucketPublicKey("pub-key").build();
        when(userBucketService.getBucketByUuid("bucket-1", userAuth)).thenReturn(new ResultDataFactoryImpl<BucketDataModel>().success(bucketData, "OK"));

        UserBucket bucket = UserBucket.builder().id("bucket-1").build();
        UserPasswordKey key = UserPasswordKey.builder()
                .id(1L)
                .userBucket(bucket)
                .type(UserPasswordKeyType.builder().id(4L).description("APPLICATION").build())
                .build();
        when(userPasswordKeyRepository.findByBucketUuidAndId("bucket-1", 1L)).thenReturn(Optional.of(key));

        EmailSecurityQuestionStoredValueDto dto = EmailSecurityQuestionStoredValueDto.builder().build();
        IResultData<KeyStorePayloadEncodedDto> result = service.createSecurityQuestionStoredKey(userAuth, "bucket-1", 1L, dto);

        assertThat(result.hasError()).isTrue();
        assertThat(result.getException()).isInstanceOf(KeyRegistrationErrorException.class);
    }

    @DisplayName("Should save/update password stored key successfully")
    @Test
    void shouldSavePasswordStoredKeySuccessfully() {
        BucketDataModel bucketData = BucketDataModel.builder().bucketUuid("bucket-1").bucketPublicKey("pub-key").build();
        when(userBucketService.getBucketByUuid("bucket-1", userAuth)).thenReturn(new ResultDataFactoryImpl<BucketDataModel>().success(bucketData, "OK"));

        UserBucket bucket = UserBucket.builder().id("bucket-1").build();
        UserPasswordKey key = UserPasswordKey.builder()
                .id(1L)
                .userBucket(bucket)
                .type(UserPasswordKeyType.builder().id(3L).description("WEBSITE").build())
                .build();
        when(userPasswordKeyRepository.findByBucketUuidAndId("bucket-1", 1L)).thenReturn(Optional.of(key));

        UserPasswordStoredValue storedVal = UserPasswordStoredValue.builder()
                .id(15L)
                .data("old")
                .keyId(key)
                .build();
        when(userPasswordStoredValueRepository.findByKeyIdAndId(key, 15L)).thenReturn(Optional.of(storedVal));

        when(cryptService.encrypt(any(), eq("pub-key"))).thenReturn("newEnc".getBytes());
        when(cryptService.convertByteToBase64("newEnc".getBytes())).thenReturn("newEncBase64");
        when(userPasswordStoredValueRepository.save(storedVal)).thenReturn(storedVal);

        WebsitePasswordStoredValueDto dto = WebsitePasswordStoredValueDto.builder()
                .password("newPass")
                .build();

        IResultData<KeyStorePayloadEncodedDto> result = service.savePasswordStoredKey(userAuth, "bucket-1", 1L, 15L, dto);

        assertThat(result.isOk()).isTrue();
        assertThat(storedVal.getData()).isEqualTo("newEncBase64");
    }

    @DisplayName("Should save/update security question stored key successfully")
    @Test
    void shouldSaveStoredQuestionStoredKeySuccessfully() {
        BucketDataModel bucketData = BucketDataModel.builder().bucketUuid("bucket-1").bucketPublicKey("pub-key").build();
        when(userBucketService.getBucketByUuid("bucket-1", userAuth)).thenReturn(new ResultDataFactoryImpl<BucketDataModel>().success(bucketData, "OK"));

        UserBucket bucket = UserBucket.builder().id("bucket-1").build();
        UserPasswordKey key = UserPasswordKey.builder()
                .id(1L)
                .userBucket(bucket)
                .type(UserPasswordKeyType.builder().id(2L).description("SOCIAL_MEDIA").build())
                .build();
        when(userPasswordKeyRepository.findByBucketUuidAndId("bucket-1", 1L)).thenReturn(Optional.of(key));

        UserPasswordStoredValue storedVal = UserPasswordStoredValue.builder()
                .id(25L)
                .data("old")
                .keyId(key)
                .build();
        when(userPasswordStoredValueRepository.findByKeyIdAndId(key, 25L)).thenReturn(Optional.of(storedVal));

        when(cryptService.encrypt(any(), eq("pub-key"))).thenReturn("newEnc".getBytes());
        when(cryptService.convertByteToBase64("newEnc".getBytes())).thenReturn("newEncBase64");
        when(userPasswordStoredValueRepository.save(storedVal)).thenReturn(storedVal);

        SocialMediaSecurityQuestionStoredValueDto dto = SocialMediaSecurityQuestionStoredValueDto.builder()
                .question("Q?")
                .expectedValue("A")
                .build();

        IResultData<KeyStorePayloadEncodedDto> result = service.saveStoredQuestionStoredKey(userAuth, "bucket-1", 1L, 25L, dto);

        assertThat(result.isOk()).isTrue();
        assertThat(storedVal.getData()).isEqualTo("newEncBase64");
    }

    @DisplayName("Should get stored key successfully")
    @Test
    void shouldGetStoredKeySuccessfully() {
        BucketDataModel bucketData = BucketDataModel.builder().bucketUuid("bucket-1").build();
        when(userBucketService.getBucketByUuid("bucket-1", userAuth)).thenReturn(new ResultDataFactoryImpl<BucketDataModel>().success(bucketData, "OK"));

        UserBucket bucket = UserBucket.builder().id("bucket-1").build();
        UserPasswordKey key = UserPasswordKey.builder().id(1L).userBucket(bucket).build();
        when(userPasswordKeyRepository.findByBucketUuidAndId("bucket-1", 1L)).thenReturn(Optional.of(key));

        UserPasswordStoredValue storedVal = UserPasswordStoredValue.builder()
                .id(10L)
                .data("encData")
                .createdAt(LocalDateTime.now())
                .lastUpdate(LocalDateTime.now())
                .build();
        when(userPasswordStoredValueRepository.findByKeyIdAndId(key, 10L)).thenReturn(Optional.of(storedVal));

        IResultData<KeyStorePayloadEncodedDto> result = service.getStoredKey(userAuth, "bucket-1", 1L, 10L);

        assertThat(result.isOk()).isTrue();
        assertThat(result.getData().getId()).isEqualTo(10L);
        assertThat(result.getData().getData()).isEqualTo("encData");
    }

    @DisplayName("Should return error on getStoredKey when stored value not found")
    @Test
    void shouldReturnErrorWhenStoredValueNotFound() {
        BucketDataModel bucketData = BucketDataModel.builder().bucketUuid("bucket-1").build();
        when(userBucketService.getBucketByUuid("bucket-1", userAuth)).thenReturn(new ResultDataFactoryImpl<BucketDataModel>().success(bucketData, "OK"));

        UserBucket bucket = UserBucket.builder().id("bucket-1").build();
        UserPasswordKey key = UserPasswordKey.builder().id(1L).userBucket(bucket).build();
        when(userPasswordKeyRepository.findByBucketUuidAndId("bucket-1", 1L)).thenReturn(Optional.of(key));
        when(userPasswordStoredValueRepository.findByKeyIdAndId(key, 10L)).thenReturn(Optional.empty());

        IResultData<KeyStorePayloadEncodedDto> result = service.getStoredKey(userAuth, "bucket-1", 1L, 10L);

        assertThat(result.hasError()).isTrue();
        assertThat(result.getException()).isInstanceOf(KeyNotFoundException.class);
    }

    @DisplayName("Should delete stored key successfully")
    @Test
    void shouldDeleteStoredKeySuccessfully() {
        BucketDataModel bucketData = BucketDataModel.builder().bucketUuid("bucket-1").build();
        when(userBucketService.getBucketByUuid("bucket-1", userAuth)).thenReturn(new ResultDataFactoryImpl<BucketDataModel>().success(bucketData, "OK"));

        UserBucket bucket = UserBucket.builder().id("bucket-1").build();
        UserPasswordKey key = UserPasswordKey.builder().id(1L).userBucket(bucket).build();
        when(userPasswordKeyRepository.findByBucketUuidAndId("bucket-1", 1L)).thenReturn(Optional.of(key));

        UserPasswordStoredValue storedVal = UserPasswordStoredValue.builder().id(10L).build();
        when(userPasswordStoredValueRepository.findByKeyIdAndId(key, 10L)).thenReturn(Optional.of(storedVal));

        IResultData<Void> result = service.deleteStoredKey(userAuth, "bucket-1", 1L, 10L);

        assertThat(result.isOk()).isTrue();
        verify(userPasswordStoredValueRepository).deleteById(10L);
    }

    @DisplayName("Should encrypt base64 using bucket successfully")
    @Test
    void shouldEncryptBase64UsingBucketSuccessfully() {
        BucketDataModel bucketData = BucketDataModel.builder().bucketUuid("bucket-1").bucketPublicKey("pub-key").build();
        when(userBucketService.getBucketByUuid("bucket-1", userAuth)).thenReturn(new ResultDataFactoryImpl<BucketDataModel>().success(bucketData, "OK"));

        String inputBase64 = Base64.getEncoder().encodeToString("test data".getBytes());
        when(cryptService.encrypt("test data".getBytes(), "pub-key")).thenReturn("encrypted".getBytes());

        IResultData<String> result = service.encryptBase64UsingBucket(userAuth, "bucket-1", inputBase64);

        assertThat(result.isOk()).isTrue();
        assertThat(result.getData()).isEqualTo(Base64.getEncoder().encodeToString("encrypted".getBytes()));
    }

    @DisplayName("Should test all key type checks")
    @Test
    void shouldVerifyAllKeyTypeChecks() {
        UserPasswordKey emailKey = UserPasswordKey.builder().type(UserPasswordKeyType.builder().id(1L).build()).build();
        UserPasswordKey socialKey = UserPasswordKey.builder().type(UserPasswordKeyType.builder().id(2L).build()).build();
        UserPasswordKey websiteKey = UserPasswordKey.builder().type(UserPasswordKeyType.builder().id(3L).build()).build();
        UserPasswordKey appKey = UserPasswordKey.builder().type(UserPasswordKeyType.builder().id(4L).build()).build();

        // Valid combinations
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> {
            service.checkIfKeyStoredIsValidForKey(emailKey, EmailPasswordStoredValueDto.builder().build());
            service.checkIfKeyStoredIsValidForKey(socialKey, SocialMediaPasswordStoredValueDto.builder().build());
            service.checkIfKeyStoredIsValidForKey(websiteKey, WebsitePasswordStoredValueDto.builder().build());
            service.checkIfKeyStoredIsValidForKey(appKey, ApplicationPasswordStoredValueDto.builder().build());

            service.checkIfKeyStoredIsValidForKey(emailKey, EmailSecurityQuestionStoredValueDto.builder().build());
            service.checkIfKeyStoredIsValidForKey(socialKey, SocialMediaSecurityQuestionStoredValueDto.builder().build());
        });
    }
}
