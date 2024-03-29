package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.models.BucketDataModel;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.models.BucketNewDataModel;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.models.BucketUpdateDataModel;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.common.IResultData;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.UserAuthorizations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserBucketService {
    IResultData<BucketDataModel> getBucketByUuid(String bucketUuid, UserAuthorizations userAuthorizations);
    IResultData<Page<BucketDataModel>> getBucketsByUserAuthorizations(UserAuthorizations userAuthorizations, Pageable pageRequest);

    IResultData<BucketDataModel> createBucket(BucketNewDataModel bucketNewDataModel, UserAuthorizations userAuthorizations);

    IResultData<BucketDataModel> updateBucket(String bucketUuid, BucketUpdateDataModel bucketUpdateDataModel, UserAuthorizations userAuthorizations);

    IResultData<Boolean> deleteBucketByUuid(String bucketUuid, UserAuthorizations userAuthorizations);
}
