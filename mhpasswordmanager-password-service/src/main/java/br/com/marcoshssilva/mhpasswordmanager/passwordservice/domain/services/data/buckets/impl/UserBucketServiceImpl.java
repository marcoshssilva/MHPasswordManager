package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.impl;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities.UserBucket;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.repositories.UserBucketRepository;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt.CryptService;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.UserBucketService;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.exceptions.BucketCannotBeCreatedException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.exceptions.BucketCannotBeDeletedException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.exceptions.BucketCannotBeUpdatedException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.exceptions.BucketNotFoundException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.models.BucketDataModel;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.models.BucketNewDataModel;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.models.BucketUpdateDataModel;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.common.IResultData;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.common.IResultDataFactory;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.common.impl.ResultDataFactoryImpl;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.UserAuthorizations;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.exceptions.UserRegistrationDeniedAccessException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserBucketServiceImpl implements UserBucketService {
    public static final IResultDataFactory<Page<BucketDataModel>> factoryPageBucketDataModel = new ResultDataFactoryImpl<>();
    public static final IResultDataFactory<BucketDataModel> factoryBucketDataModel = new ResultDataFactoryImpl<>();
    public static final IResultDataFactory<Boolean> factoryBoolean = new ResultDataFactoryImpl<>();

    private final UserBucketRepository userBucketRepository;
    private final CryptService cryptService;

    public UserBucketServiceImpl(UserBucketRepository userBucketRepository, @Qualifier("aesCryptService") CryptService cryptService) {
        this.userBucketRepository = userBucketRepository;
        this.cryptService = cryptService;
    }

    @Override
    public IResultData<BucketDataModel> getBucketByUuid(String bucketUuid, UserAuthorizations userAuthorizations) {
        try {
            Optional<UserBucket> userBucket = userBucketRepository.findById(bucketUuid);
            // case not exists
            if (userBucket.isEmpty()) {
                return factoryBucketDataModel.exception(new BucketNotFoundException(), "Bucket not found");
            }
            // case is from another user
            if (!Objects.equals(userBucket.get().getOwnerName(), userAuthorizations.getUsername())) {
                return factoryBucketDataModel.exception(new UserRegistrationDeniedAccessException(), "Denied access to User");
            }

            return factoryBucketDataModel.success(BucketDataModel.fromEntity(userBucket.get()), "SUCCESS");
        } catch (Exception e) {
            return factoryBucketDataModel.exception(e, e.getMessage());
        }

    }

    @Override
    public IResultData<Page<BucketDataModel>> getBucketsByUserAuthorizations(UserAuthorizations userAuthorizations, Pageable pageRequest) {
        try {
            final Page<UserBucket> page = userBucketRepository.findAllByOwnerName(userAuthorizations.getUsername(), pageRequest);
            return factoryPageBucketDataModel.success(page.map(BucketDataModel::fromEntity), "SUCCESS");
        } catch (Exception e) {
            return factoryPageBucketDataModel.exception(e, "Cannot fetch buckets, because: " + e.getMessage());
        }
    }

    @Override
    public IResultData<BucketDataModel> createBucket(BucketNewDataModel bucketNewDataModel, UserAuthorizations userAuthorizations) {
        try {
            if (!Objects.equals(bucketNewDataModel.getUserOwner(), userAuthorizations.getUsername())) {
                return factoryBucketDataModel.exception(new UserRegistrationDeniedAccessException(), "Denied access to User");
            }

            KeyPairGenerator rsa = KeyPairGenerator.getInstance("RSA");
            rsa.initialize(4096);
            KeyPair keyPair = rsa.generateKeyPair();

            String base64PublicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
            byte[] encryptedPrivateKey = cryptService.encrypt(keyPair.getPrivate().getEncoded(), bucketNewDataModel.getBucketSecret());

            UserBucket userBucket = new UserBucket();

            userBucket.setId(UUID.randomUUID().toString());
            userBucket.setName(bucketNewDataModel.getBucketName());
            userBucket.setDescription(bucketNewDataModel.getBucketDescription());
            userBucket.setOwnerName(userAuthorizations.getUsername());
            userBucket.setCreatedAt(new Date());
            userBucket.setLastUpdate(new Date());
            userBucket.setEncryptedPrivateKeyWithPassword(Base64.getEncoder().encodeToString(encryptedPrivateKey));
            userBucket.setEncodedPublicKey(base64PublicKey);

            final UserBucket saved = userBucketRepository.save(userBucket);

            return factoryBucketDataModel.success(BucketDataModel.fromEntity(saved), "CREATED");
        } catch (Exception e) {
            return factoryBucketDataModel.exception(new BucketCannotBeCreatedException(e), "Bucket cannot be create because: " + e.getMessage());
        }
    }

    @Override
    public IResultData<BucketDataModel> updateBucket(String bucketUuid, BucketUpdateDataModel bucketUpdateDataModel, UserAuthorizations userAuthorizations) {
        try {
            Optional<UserBucket> userBucket = userBucketRepository.findById(bucketUuid);
            // case not exists
            if (userBucket.isEmpty()) {
                return factoryBucketDataModel.exception(new BucketNotFoundException(), "Bucket not found");
            }
            // case is from another user
            final UserBucket bucket = userBucket.get();
            if (!Objects.equals(bucket.getOwnerName(), userAuthorizations.getUsername())) {
                return factoryBucketDataModel.exception(new UserRegistrationDeniedAccessException(), "Denied access to User");
            }

            bucket.setName(bucketUpdateDataModel.getBucketName());
            bucket.setDescription(bucketUpdateDataModel.getBucketDescription());
            bucket.setLastUpdate(new Date());

            final UserBucket saved = userBucketRepository.save(bucket);
            return factoryBucketDataModel.success(BucketDataModel.fromEntity(saved), "UPDATED");
        } catch (Exception e) {
            return factoryBucketDataModel.exception(new BucketCannotBeUpdatedException(e), "Bucket cannot be updated because: " + e.getMessage());
        }
    }

    @Override
    public IResultData<Boolean> deleteBucketByUuid(String bucketUuid, UserAuthorizations userAuthorizations) {
        try {
            Optional<UserBucket> userBucket = userBucketRepository.findById(bucketUuid);
            // case not exists
            if (userBucket.isEmpty()) {
                return factoryBoolean.exception(new BucketNotFoundException(), "Bucket not found");
            }
            // case is from another user
            final UserBucket bucket = userBucket.get();
            if (!Objects.equals(bucket.getOwnerName(), userAuthorizations.getUsername())) {
                return factoryBoolean.exception(new UserRegistrationDeniedAccessException(), "Denied access to User");
            }
            userBucketRepository.delete(bucket);
            return factoryBoolean.success(Boolean.TRUE, "DELETED");
        } catch (Exception e) {
            return factoryBoolean.exception(new BucketCannotBeDeletedException(e), "Bucket cannot be deleted because: " + e.getMessage());
        }
    }
}
