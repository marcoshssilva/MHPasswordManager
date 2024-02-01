package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.impl;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities.UserBucket;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.repositories.UserBucketRepository;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt.CryptService;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt.RSAUtilService;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.UserBucketService;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.exceptions.BucketCannotBeCreatedException;
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
    public static final IResultDataFactory<BucketDataModel> factory = new ResultDataFactoryImpl<>();
    private final UserBucketRepository userBucketRepository;
    private final CryptService aesCryptService;
    private final CryptService rsaCryptService;
    private final RSAUtilService rsaUtilService;

    public UserBucketServiceImpl(UserBucketRepository userBucketRepository, @Qualifier("aesCryptService") CryptService aesCryptService, @Qualifier("rsaCryptService") CryptService rsaCryptService, RSAUtilService rsaUtilService) {
        this.userBucketRepository = userBucketRepository;
        this.aesCryptService = aesCryptService;
        this.rsaCryptService = rsaCryptService;
        this.rsaUtilService = rsaUtilService;
    }

    @Override
    public IResultData<BucketDataModel> getBucketByUuid(String bucketUuid, UserAuthorizations userAuthorizations) throws BucketNotFoundException, UserRegistrationDeniedAccessException {
        IResultDataFactory<BucketDataModel> factory = new ResultDataFactoryImpl<>();
        Optional<UserBucket> userBucket;
        try {
            userBucket = userBucketRepository.findById(bucketUuid);
        } catch (Exception e) {
            return factory.exception(e, e.getMessage());
        }

        if (userBucket.isEmpty()) {
            throw new BucketNotFoundException();
        }
        if (!Objects.equals(userBucket.get().getOwnerName(), userAuthorizations.getUsername())) {
            throw new UserRegistrationDeniedAccessException();
        }

        return factory.success(BucketDataModel.fromEntity(userBucket.get()), "SUCCESS");

    }

    @Override
    public IResultData<BucketDataModel> createBucket(BucketNewDataModel bucketNewDataModel, UserAuthorizations userAuthorizations) throws BucketCannotBeCreatedException, UserRegistrationDeniedAccessException {
        if (!Objects.equals(bucketNewDataModel.getUserOwner(), userAuthorizations.getUsername())) {
            throw new UserRegistrationDeniedAccessException();
        }

        try {
            KeyPairGenerator rsa = KeyPairGenerator.getInstance("RSA");
            rsa.initialize(4096);
            KeyPair keyPair = rsa.generateKeyPair();

            String base64PublicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
            byte[] encryptedPrivateKey = aesCryptService.encrypt(keyPair.getPrivate().getEncoded(), bucketNewDataModel.getBucketSecret());

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

            return factory.success(BucketDataModel.fromEntity(saved), "CREATED");
        } catch (Exception e) {
            throw new BucketCannotBeCreatedException(e);
        }
    }

    @Override
    public IResultData<BucketDataModel> updateBucket(String bucketUuid, BucketUpdateDataModel bucketUpdateDataModel, UserAuthorizations userAuthorizations) throws BucketNotFoundException, UserRegistrationDeniedAccessException {
        return null;
    }

    @Override
    public IResultData<Boolean> deleteBucketByUuid(String bucketUuid, UserAuthorizations userAuthorizations) throws BucketNotFoundException, UserRegistrationDeniedAccessException {
        return null;
    }
}
