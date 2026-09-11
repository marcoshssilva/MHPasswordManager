package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.impl;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities.UserBucket;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities.UserPasswordKey;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities.UserPasswordKeyType;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities.UserPasswordStoredValue;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.enums.PasswordKeyTypesEnum;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.repositories.UserPasswordKeyRepository;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.repositories.UserPasswordStoredValueRepository;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.UserBucketService;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.models.BucketDataModel;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.common.IResultData;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.common.impl.ResultDataFactoryImpl;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.converters.*;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.exceptions.KeyNotFoundException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.AbstractKeyPayloadDecodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.KeyPayloadEncodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.KeyStorePayloadEncodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.application.ApplicationPayloadDecodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.bank.BankCardPayloadDecodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.email.EmailPayloadDecodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.social.SocialMediaPayloadDecodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.website.WebsitePayloadDecodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.UserAuthorizations;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.models.UserAuthorizationModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserKeysServiceImplTests {

    @Mock
    private UserPasswordStoredValueRepository userPasswordStoredValueRepository;

    @Mock
    private UserPasswordKeyRepository userPasswordKeyRepository;

    @Mock
    private UserBucketService userBucketService;

    @Mock
    private ApplicationKeyToEncodedConverter applicationKeyToEncodedConverter;

    @Mock
    private BankCardKeyToEncodedConverter bankCardKeyToEncodedConverter;

    @Mock
    private EmailKeyToEncodedConverter emailKeyToEncodedConverter;

    @Mock
    private SocialMediaKeyToEncodedConverter socialMediaKeyToEncodedConverter;

    @Mock
    private WebsiteKeyDecodedToEncodedConverter websiteKeyDecodedToEncodedConverter;

    private UserKeysServiceImpl userKeysService;
    private UserAuthorizations userAuth;

    @BeforeEach
    void setUp() {
        userKeysService = new UserKeysServiceImpl(
                userPasswordStoredValueRepository,
                userPasswordKeyRepository,
                userBucketService,
                applicationKeyToEncodedConverter,
                bankCardKeyToEncodedConverter,
                emailKeyToEncodedConverter,
                socialMediaKeyToEncodedConverter,
                websiteKeyDecodedToEncodedConverter
        );
        userAuth = UserAuthorizationModel.builder().username("user1").roles(Collections.emptySet()).profiles(Collections.emptySet()).build();
    }

    @DisplayName("Should get encoded key from bucket successfully")
    @Test
    void shouldGetEncodedKeyFromBucketSuccessfully() {
        BucketDataModel bucketData = BucketDataModel.builder().bucketUuid("bucket-1").build();
        when(userBucketService.getBucketByUuid("bucket-1", userAuth)).thenReturn(new ResultDataFactoryImpl<BucketDataModel>().success(bucketData, "OK"));

        UserBucket bucket = UserBucket.builder().id("bucket-1").build();
        UserPasswordKey key = UserPasswordKey.builder()
                .id(1L)
                .userBucket(bucket)
                .description("Desc")
                .createdAt(LocalDateTime.now())
                .lastUpdate(LocalDateTime.now())
                .tags(Set.of("tag1"))
                .type(UserPasswordKeyType.builder().id(1L).description("APPLICATION").build())
                .build();
        when(userPasswordKeyRepository.findByBucketUuidAndId("bucket-1", 1L)).thenReturn(Optional.of(key));

        UserPasswordStoredValue storedVal = UserPasswordStoredValue.builder()
                .id(10L)
                .data("encData")
                .createdAt(LocalDateTime.now())
                .lastUpdate(LocalDateTime.now())
                .build();
        when(userPasswordStoredValueRepository.findAllByKeyId(key)).thenReturn(Set.of(storedVal));

        IResultData<KeyPayloadEncodedDto> result = userKeysService.getEncodedKeyFromBucket(userAuth, "bucket-1", 1L);

        assertThat(result.isOk()).isTrue();
        assertThat(result.getData().getId()).isEqualTo(1L);
        assertThat(result.getData().getOwnerId()).isEqualTo("bucket-1");
        assertThat(result.getData().getEncodedKeys()).hasSize(1);
        assertThat(result.getData().getEncodedKeys()[0].getId()).isEqualTo(10L);
    }

    @DisplayName("Should return error when bucket fails in getEncodedKeyFromBucket")
    @Test
    void shouldReturnErrorWhenBucketFailsInGetEncodedKeyFromBucket() {
        when(userBucketService.getBucketByUuid("bucket-1", userAuth)).thenReturn(new ResultDataFactoryImpl<BucketDataModel>().error("Bucket error"));

        IResultData<KeyPayloadEncodedDto> result = userKeysService.getEncodedKeyFromBucket(userAuth, "bucket-1", 1L);

        assertThat(result.hasError()).isTrue();
        assertThat(result.getMessage()).isEqualTo("Bucket error");
    }

    @DisplayName("Should return KeyNotFoundException when key not found in bucket")
    @Test
    void shouldReturnNotFoundWhenKeyNotFoundInBucket() {
        BucketDataModel bucketData = BucketDataModel.builder().bucketUuid("bucket-1").build();
        when(userBucketService.getBucketByUuid("bucket-1", userAuth)).thenReturn(new ResultDataFactoryImpl<BucketDataModel>().success(bucketData, "OK"));
        when(userPasswordKeyRepository.findByBucketUuidAndId("bucket-1", 1L)).thenReturn(Optional.empty());

        IResultData<KeyPayloadEncodedDto> result = userKeysService.getEncodedKeyFromBucket(userAuth, "bucket-1", 1L);

        assertThat(result.hasError()).isTrue();
        assertThat(result.getException()).isInstanceOf(KeyNotFoundException.class);
    }

    @DisplayName("Should get all encoded keys from bucket")
    @Test
    void shouldGetAllEncodedKeysFromBucket() {
        BucketDataModel bucketData = BucketDataModel.builder().bucketUuid("bucket-1").build();
        when(userBucketService.getBucketByUuid("bucket-1", userAuth)).thenReturn(new ResultDataFactoryImpl<BucketDataModel>().success(bucketData, "OK"));

        UserBucket bucket = UserBucket.builder().id("bucket-1").build();
        UserPasswordKey key = UserPasswordKey.builder()
                .id(1L)
                .userBucket(bucket)
                .description("Desc")
                .createdAt(LocalDateTime.now())
                .lastUpdate(LocalDateTime.now())
                .tags(Set.of("tag1"))
                .type(UserPasswordKeyType.builder().id(1L).description("APPLICATION").build())
                .build();
        Pageable pageable = PageRequest.of(0, 10);
        when(userPasswordKeyRepository.findAllByBucketUuid("bucket-1", pageable)).thenReturn(new PageImpl<>(List.of(key)));
        when(userPasswordStoredValueRepository.findAllByKeyId(key)).thenReturn(Collections.emptySet());

        IResultData<Page<KeyPayloadEncodedDto>> result = userKeysService.getAllEncodedKeyFromBucket(userAuth, "bucket-1", pageable);

        assertThat(result.isOk()).isTrue();
        assertThat(result.getData().getTotalElements()).isEqualTo(1);
    }

    @DisplayName("Should return error when bucket fails in getAllEncodedKeyFromBucket")
    @Test
    void shouldReturnErrorWhenBucketFailsInGetAllKeys() {
        when(userBucketService.getBucketByUuid("bucket-1", userAuth)).thenReturn(new ResultDataFactoryImpl<BucketDataModel>().error("Bucket error"));

        IResultData<Page<KeyPayloadEncodedDto>> result = userKeysService.getAllEncodedKeyFromBucket(userAuth, "bucket-1", PageRequest.of(0, 10));

        assertThat(result.hasError()).isTrue();
    }

    @DisplayName("Should save key payload encoded DTO successfully")
    @Test
    void shouldSaveKeyPayloadEncodedDtoSuccessfully() {
        BucketDataModel bucketData = BucketDataModel.builder().bucketUuid("bucket-1").build();
        when(userBucketService.getBucketByUuid("bucket-1", userAuth)).thenReturn(new ResultDataFactoryImpl<BucketDataModel>().success(bucketData, "OK"));

        UserBucket bucket = UserBucket.builder().id("bucket-1").build();
        UserPasswordKey savedKey = UserPasswordKey.builder()
                .id(100L)
                .userBucket(bucket)
                .description("Saved Key")
                .type(UserPasswordKeyType.builder().id(1L).description("APPLICATION").build())
                .tags(Set.of("tag"))
                .createdAt(LocalDateTime.now())
                .lastUpdate(LocalDateTime.now())
                .build();
        when(userPasswordKeyRepository.save(any(UserPasswordKey.class))).thenReturn(savedKey);
        when(userPasswordStoredValueRepository.save(any(UserPasswordStoredValue.class))).thenAnswer(i -> i.getArgument(0));

        when(userPasswordKeyRepository.findByBucketUuidAndId("bucket-1", 100L)).thenReturn(Optional.of(savedKey));
        when(userPasswordStoredValueRepository.findAllByKeyId(savedKey)).thenReturn(Collections.emptySet());

        KeyPayloadEncodedDto dto = KeyPayloadEncodedDto.builder()
                .ownerId("bucket-1")
                .description("Saved Key")
                .tags(new String[]{"tag"})
                .type(PasswordKeyTypesEnum.APPLICATION)
                .encodedKeys(new KeyStorePayloadEncodedDto[]{
                        KeyStorePayloadEncodedDto.builder().data("data").build()
                })
                .build();

        IResultData<KeyPayloadEncodedDto> result = userKeysService.saveKeyPayloadEncodedDto(userAuth, dto);

        assertThat(result.isOk()).isTrue();
        assertThat(result.getData().getId()).isEqualTo(100L);
    }

    @DisplayName("Should return error when saveKeyPayloadEncodedDto fails due to bucket error")
    @Test
    void shouldReturnErrorWhenSaveFailsBucket() {
        when(userBucketService.getBucketByUuid("bucket-1", userAuth)).thenReturn(new ResultDataFactoryImpl<BucketDataModel>().error("Bucket not found"));

        KeyPayloadEncodedDto dto = KeyPayloadEncodedDto.builder().ownerId("bucket-1").build();

        IResultData<KeyPayloadEncodedDto> result = userKeysService.saveKeyPayloadEncodedDto(userAuth, dto);

        assertThat(result.hasError()).isTrue();
    }

    @DisplayName("Should update key payload encoded DTO successfully")
    @Test
    void shouldUpdateKeyPayloadEncodedDtoSuccessfully() {
        BucketDataModel bucketData = BucketDataModel.builder().bucketUuid("bucket-1").build();
        when(userBucketService.getBucketByUuid("bucket-1", userAuth)).thenReturn(new ResultDataFactoryImpl<BucketDataModel>().success(bucketData, "OK"));

        UserBucket bucket = UserBucket.builder().id("bucket-1").build();
        UserPasswordKey existingKey = UserPasswordKey.builder()
                .id(1L)
                .userBucket(bucket)
                .description("Old")
                .type(UserPasswordKeyType.builder().id(1L).description("APPLICATION").build())
                .tags(Set.of("tag"))
                .createdAt(LocalDateTime.now())
                .lastUpdate(LocalDateTime.now())
                .build();
        when(userPasswordKeyRepository.findByBucketUuidAndId("bucket-1", 1L)).thenReturn(Optional.of(existingKey));

        UserPasswordStoredValue existingStored = UserPasswordStoredValue.builder()
                .id(10L)
                .data("oldData")
                .keyId(existingKey)
                .createdAt(LocalDateTime.now())
                .lastUpdate(LocalDateTime.now())
                .build();
        when(userPasswordStoredValueRepository.findAllByKeyId(existingKey)).thenReturn(Set.of(existingStored));

        when(userPasswordKeyRepository.save(any(UserPasswordKey.class))).thenReturn(existingKey);
        when(userPasswordStoredValueRepository.save(any(UserPasswordStoredValue.class))).thenReturn(existingStored);

        KeyPayloadEncodedDto updateDto = KeyPayloadEncodedDto.builder()
                .id(1L)
                .ownerId("bucket-1")
                .description("New Desc")
                .tags(new String[]{"newTag"})
                .type(PasswordKeyTypesEnum.APPLICATION)
                .encodedKeys(new KeyStorePayloadEncodedDto[]{
                        KeyStorePayloadEncodedDto.builder().id(10L).data("newData").build()
                })
                .build();

        IResultData<KeyPayloadEncodedDto> result = userKeysService.updateKeyPayloadEncodedDto(userAuth, updateDto);

        assertThat(result.isOk()).isTrue();
        assertThat(result.getMessage()).isEqualTo("UPDATED");
    }

    @DisplayName("Should transform AbstractKeyPayloadDecodedDto instances correctly")
    @Test
    void shouldTransformDecodedDtos() throws Exception {
        KeyPayloadEncodedDto encodedMock = KeyPayloadEncodedDto.builder().id(1L).build();

        when(applicationKeyToEncodedConverter.convert(any(), eq("sec"))).thenReturn(encodedMock);
        when(bankCardKeyToEncodedConverter.convert(any(), eq("sec"))).thenReturn(encodedMock);
        when(emailKeyToEncodedConverter.convert(any(), eq("sec"))).thenReturn(encodedMock);
        when(socialMediaKeyToEncodedConverter.convert(any(), eq("sec"))).thenReturn(encodedMock);
        when(websiteKeyDecodedToEncodedConverter.convert(any(), eq("sec"))).thenReturn(encodedMock);

        assertThat(userKeysService.transformAsKeyPayloadEncodedDto(ApplicationPayloadDecodedDto.builder().build(), "sec").isOk()).isTrue();
        assertThat(userKeysService.transformAsKeyPayloadEncodedDto(BankCardPayloadDecodedDto.builder().build(), "sec").isOk()).isTrue();
        assertThat(userKeysService.transformAsKeyPayloadEncodedDto(EmailPayloadDecodedDto.builder().build(), "sec").isOk()).isTrue();
        assertThat(userKeysService.transformAsKeyPayloadEncodedDto(SocialMediaPayloadDecodedDto.builder().build(), "sec").isOk()).isTrue();
        assertThat(userKeysService.transformAsKeyPayloadEncodedDto(WebsitePayloadDecodedDto.builder().build(), "sec").isOk()).isTrue();

        class UnknownDto extends AbstractKeyPayloadDecodedDto {
            @Override
            public PasswordKeyTypesEnum getType() {
                return null;
            }
        }
        IResultData<KeyPayloadEncodedDto> unknownResult = userKeysService.transformAsKeyPayloadEncodedDto(new UnknownDto(), "sec");
        assertThat(unknownResult.hasError()).isTrue();
    }

    @DisplayName("Should delete key payload and its stored values")
    @Test
    void shouldDeleteKeyPayloadSuccessfully() {
        BucketDataModel bucketData = BucketDataModel.builder().bucketUuid("bucket-1").build();
        when(userBucketService.getBucketByUuid("bucket-1", userAuth)).thenReturn(new ResultDataFactoryImpl<BucketDataModel>().success(bucketData, "OK"));

        UserBucket bucket = UserBucket.builder().id("bucket-1").build();
        UserPasswordKey key = UserPasswordKey.builder()
                .id(1L)
                .userBucket(bucket)
                .description("Desc")
                .createdAt(LocalDateTime.now())
                .lastUpdate(LocalDateTime.now())
                .tags(Set.of("tag1"))
                .type(UserPasswordKeyType.builder().id(1L).description("APPLICATION").build())
                .build();
        when(userPasswordKeyRepository.findByBucketUuidAndId("bucket-1", 1L)).thenReturn(Optional.of(key));

        UserPasswordStoredValue storedVal = UserPasswordStoredValue.builder()
                .id(10L)
                .data("encData")
                .build();
        when(userPasswordStoredValueRepository.findAllByKeyId(key)).thenReturn(Set.of(storedVal));

        IResultData<Void> result = userKeysService.deleteKeyPayload(userAuth, "bucket-1", 1L);

        assertThat(result.isOk()).isTrue();
        verify(userPasswordStoredValueRepository).deleteById(10L);
        verify(userPasswordKeyRepository).deleteById(1L);
    }

    @DisplayName("Should return error on deleteKeyPayload when key is not found")
    @Test
    void shouldReturnErrorOnDeleteKeyPayloadWhenNotFound() {
        BucketDataModel bucketData = BucketDataModel.builder().bucketUuid("bucket-1").build();
        when(userBucketService.getBucketByUuid("bucket-1", userAuth)).thenReturn(new ResultDataFactoryImpl<BucketDataModel>().success(bucketData, "OK"));
        when(userPasswordKeyRepository.findByBucketUuidAndId("bucket-1", 1L)).thenReturn(Optional.empty());

        IResultData<Void> result = userKeysService.deleteKeyPayload(userAuth, "bucket-1", 1L);

        assertThat(result.hasError()).isTrue();
        verify(userPasswordKeyRepository, never()).deleteById(any());
    }
}
