package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.impl;

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
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.UserAuthorizations;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.exceptions.UserRegistrationDeniedAccessException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class UserBucketServiceImpl implements UserBucketService {
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
        return null;
    }

    @Override
    public IResultData<BucketDataModel> createBucket(BucketNewDataModel bucketNewDataModel, UserAuthorizations userAuthorizations) throws BucketCannotBeCreatedException, UserRegistrationDeniedAccessException {
        return null;
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
