package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.impl;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities.UserBucket;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.repositories.UserBucketRepository;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt.CryptService;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.exceptions.BucketCannotBeCreatedException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.exceptions.BucketCannotBeDeletedException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.exceptions.BucketCannotBeUpdatedException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.exceptions.BucketNotFoundException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.models.BucketDataModel;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.models.BucketNewDataModel;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.models.BucketUpdateDataModel;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.common.IResultData;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.UserAuthorizations;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.exceptions.UserRegistrationDeniedAccessException;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserBucketServiceImplTests {

    @Mock
    private UserBucketRepository userBucketRepository;

    @Mock
    private CryptService aesCryptService;

    private UserBucketServiceImpl userBucketService;

    private UserAuthorizations userAuth;

    @BeforeEach
    void setUp() {
        userBucketService = new UserBucketServiceImpl(userBucketRepository, aesCryptService);
        userAuth = UserAuthorizationModel.builder()
                .username("user1")
                .roles(Collections.emptySet())
                .profiles(Collections.emptySet())
                .build();
    }

    @DisplayName("Should get bucket by uuid successfully when user is the owner")
    @Test
    void shouldGetBucketByUuidSuccessfully() {
        UserBucket bucket = UserBucket.builder()
                .id("bucket-1")
                .name("My Bucket")
                .description("Desc")
                .ownerName("user1")
                .createdAt(LocalDateTime.now())
                .lastUpdate(LocalDateTime.now())
                .encodedPublicKey("pub-key")
                .encryptedPrivateKeyWithPassword("priv-key")
                .build();

        when(userBucketRepository.findById("bucket-1")).thenReturn(Optional.of(bucket));

        IResultData<BucketDataModel> result = userBucketService.getBucketByUuid("bucket-1", userAuth);

        assertThat(result.isOk()).isTrue();
        assertThat(result.getData().getBucketUuid()).isEqualTo("bucket-1");
        assertThat(result.getData().getBucketName()).isEqualTo("My Bucket");
        assertThat(result.getData().getBucketPublicKey()).isEqualTo("pub-key");
    }

    @DisplayName("Should return error when bucket not found by uuid")
    @Test
    void shouldReturnErrorWhenBucketNotFound() {
        when(userBucketRepository.findById("not-found")).thenReturn(Optional.empty());

        IResultData<BucketDataModel> result = userBucketService.getBucketByUuid("not-found", userAuth);

        assertThat(result.hasError()).isTrue();
        assertThat(result.getException()).isInstanceOf(BucketNotFoundException.class);
    }

    @DisplayName("Should return error when user is not owner of the bucket")
    @Test
    void shouldReturnErrorWhenNotOwner() {
        UserBucket bucket = UserBucket.builder()
                .id("bucket-2")
                .ownerName("other-user")
                .build();

        when(userBucketRepository.findById("bucket-2")).thenReturn(Optional.of(bucket));

        IResultData<BucketDataModel> result = userBucketService.getBucketByUuid("bucket-2", userAuth);

        assertThat(result.hasError()).isTrue();
        assertThat(result.getException()).isInstanceOf(UserRegistrationDeniedAccessException.class);
    }

    @DisplayName("Should return error when repository throws exception on getBucketByUuid")
    @Test
    void shouldHandleExceptionOnGetBucketByUuid() {
        when(userBucketRepository.findById("bucket-err")).thenThrow(new RuntimeException("DB error"));

        IResultData<BucketDataModel> result = userBucketService.getBucketByUuid("bucket-err", userAuth);

        assertThat(result.hasError()).isTrue();
        assertThat(result.getMessage()).isEqualTo("DB error");
    }

    @DisplayName("Should get all buckets by user authorizations")
    @Test
    void shouldGetBucketsByUserAuthorizations() {
        UserBucket bucket = UserBucket.builder()
                .id("bucket-1")
                .name("Bucket 1")
                .ownerName("user1")
                .build();
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserBucket> page = new PageImpl<>(List.of(bucket), pageable, 1);

        when(userBucketRepository.findAllByOwnerName("user1", pageable)).thenReturn(page);

        IResultData<Page<BucketDataModel>> result = userBucketService.getBucketsByUserAuthorizations(userAuth, pageable);

        assertThat(result.isOk()).isTrue();
        assertThat(result.getData().getTotalElements()).isEqualTo(1);
        assertThat(result.getData().getContent().get(0).getBucketUuid()).isEqualTo("bucket-1");
    }

    @DisplayName("Should handle exception on getBucketsByUserAuthorizations")
    @Test
    void shouldHandleExceptionOnGetBuckets() {
        Pageable pageable = PageRequest.of(0, 10);
        when(userBucketRepository.findAllByOwnerName("user1", pageable)).thenThrow(new RuntimeException("DB error"));

        IResultData<Page<BucketDataModel>> result = userBucketService.getBucketsByUserAuthorizations(userAuth, pageable);

        assertThat(result.hasError()).isTrue();
        assertThat(result.getMessage()).contains("DB error");
    }

    @DisplayName("Should create bucket successfully")
    @Test
    void shouldCreateBucketSuccessfully() {
        BucketNewDataModel model = BucketNewDataModel.builder()
                .bucketName("New Bucket")
                .bucketDescription("Description")
                .bucketSecret("secret123")
                .userOwner("user1")
                .build();

        when(aesCryptService.encrypt(any(), eq("secret123"))).thenReturn("encryptedPrivKey".getBytes());
        when(userBucketRepository.save(any(UserBucket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        IResultData<BucketDataModel> result = userBucketService.createBucket(model, userAuth);

        assertThat(result.isOk()).isTrue();
        assertThat(result.getData().getBucketName()).isEqualTo("New Bucket");
        assertThat(result.getData().getBucketDescription()).isEqualTo("Description");
        verify(userBucketRepository).save(any(UserBucket.class));
    }

    @DisplayName("Should return error on createBucket when owner mismatch")
    @Test
    void shouldReturnErrorOnCreateBucketWhenOwnerMismatch() {
        BucketNewDataModel model = BucketNewDataModel.builder()
                .userOwner("otherUser")
                .build();

        IResultData<BucketDataModel> result = userBucketService.createBucket(model, userAuth);

        assertThat(result.hasError()).isTrue();
        assertThat(result.getException()).isInstanceOf(UserRegistrationDeniedAccessException.class);
    }

    @DisplayName("Should handle exception on createBucket")
    @Test
    void shouldHandleExceptionOnCreateBucket() {
        BucketNewDataModel model = BucketNewDataModel.builder()
                .userOwner("user1")
                .bucketSecret("secret")
                .build();

        when(aesCryptService.encrypt(any(), eq("secret"))).thenThrow(new RuntimeException("Encryption error"));

        IResultData<BucketDataModel> result = userBucketService.createBucket(model, userAuth);

        assertThat(result.hasError()).isTrue();
        assertThat(result.getException()).isInstanceOf(BucketCannotBeCreatedException.class);
    }

    @DisplayName("Should update bucket successfully")
    @Test
    void shouldUpdateBucketSuccessfully() {
        UserBucket bucket = UserBucket.builder()
                .id("bucket-1")
                .name("Old Name")
                .description("Old Desc")
                .ownerName("user1")
                .createdAt(LocalDateTime.now())
                .lastUpdate(LocalDateTime.now())
                .build();

        BucketUpdateDataModel updateModel = BucketUpdateDataModel.builder()
                .bucketName("Updated Name")
                .bucketDescription("Updated Desc")
                .build();

        when(userBucketRepository.findById("bucket-1")).thenReturn(Optional.of(bucket));
        when(userBucketRepository.save(any(UserBucket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        IResultData<BucketDataModel> result = userBucketService.updateBucket("bucket-1", updateModel, userAuth);

        assertThat(result.isOk()).isTrue();
        assertThat(result.getData().getBucketName()).isEqualTo("Updated Name");
        assertThat(result.getData().getBucketDescription()).isEqualTo("Updated Desc");
    }

    @DisplayName("Should return not found error on updateBucket")
    @Test
    void shouldReturnNotFoundOnUpdateBucket() {
        when(userBucketRepository.findById("not-found")).thenReturn(Optional.empty());

        IResultData<BucketDataModel> result = userBucketService.updateBucket("not-found", BucketUpdateDataModel.builder().build(), userAuth);

        assertThat(result.hasError()).isTrue();
        assertThat(result.getException()).isInstanceOf(BucketNotFoundException.class);
    }

    @DisplayName("Should return access denied error on updateBucket when owner mismatch")
    @Test
    void shouldReturnAccessDeniedOnUpdateBucket() {
        UserBucket bucket = UserBucket.builder()
                .id("bucket-1")
                .ownerName("otherUser")
                .build();

        when(userBucketRepository.findById("bucket-1")).thenReturn(Optional.of(bucket));

        IResultData<BucketDataModel> result = userBucketService.updateBucket("bucket-1", BucketUpdateDataModel.builder().build(), userAuth);

        assertThat(result.hasError()).isTrue();
        assertThat(result.getException()).isInstanceOf(UserRegistrationDeniedAccessException.class);
    }

    @DisplayName("Should handle exception on updateBucket")
    @Test
    void shouldHandleExceptionOnUpdateBucket() {
        when(userBucketRepository.findById("bucket-1")).thenThrow(new RuntimeException("DB error"));

        IResultData<BucketDataModel> result = userBucketService.updateBucket("bucket-1", BucketUpdateDataModel.builder().build(), userAuth);

        assertThat(result.hasError()).isTrue();
        assertThat(result.getException()).isInstanceOf(BucketCannotBeUpdatedException.class);
    }

    @DisplayName("Should delete bucket successfully")
    @Test
    void shouldDeleteBucketSuccessfully() {
        UserBucket bucket = UserBucket.builder()
                .id("bucket-1")
                .ownerName("user1")
                .build();

        when(userBucketRepository.findById("bucket-1")).thenReturn(Optional.of(bucket));

        IResultData<Boolean> result = userBucketService.deleteBucketByUuid("bucket-1", userAuth);

        assertThat(result.isOk()).isTrue();
        assertThat(result.getData()).isTrue();
        verify(userBucketRepository).delete(bucket);
    }

    @DisplayName("Should return not found error on deleteBucketByUuid")
    @Test
    void shouldReturnNotFoundOnDeleteBucket() {
        when(userBucketRepository.findById("not-found")).thenReturn(Optional.empty());

        IResultData<Boolean> result = userBucketService.deleteBucketByUuid("not-found", userAuth);

        assertThat(result.hasError()).isTrue();
        assertThat(result.getException()).isInstanceOf(BucketNotFoundException.class);
    }

    @DisplayName("Should return access denied error on deleteBucketByUuid when owner mismatch")
    @Test
    void shouldReturnAccessDeniedOnDeleteBucket() {
        UserBucket bucket = UserBucket.builder()
                .id("bucket-1")
                .ownerName("otherUser")
                .build();

        when(userBucketRepository.findById("bucket-1")).thenReturn(Optional.of(bucket));

        IResultData<Boolean> result = userBucketService.deleteBucketByUuid("bucket-1", userAuth);

        assertThat(result.hasError()).isTrue();
        assertThat(result.getException()).isInstanceOf(UserRegistrationDeniedAccessException.class);
    }

    @DisplayName("Should handle exception on deleteBucketByUuid")
    @Test
    void shouldHandleExceptionOnDeleteBucket() {
        when(userBucketRepository.findById("bucket-1")).thenThrow(new RuntimeException("DB error"));

        IResultData<Boolean> result = userBucketService.deleteBucketByUuid("bucket-1", userAuth);

        assertThat(result.hasError()).isTrue();
        assertThat(result.getException()).isInstanceOf(BucketCannotBeDeletedException.class);
    }
}
