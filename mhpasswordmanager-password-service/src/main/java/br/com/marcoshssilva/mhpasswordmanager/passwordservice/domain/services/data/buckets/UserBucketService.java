package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.exceptions.BucketCannotBeCreatedException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.exceptions.BucketNotFoundException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.models.BucketDataModel;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.models.BucketNewDataModel;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.models.BucketUpdateDataModel;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.common.IResultData;

public interface UserBucketService {
    IResultData<BucketDataModel> getBucketByUuid(String bucketUuid) throws BucketNotFoundException;
    IResultData<BucketDataModel> createBucket(BucketNewDataModel bucketNewDataModel) throws BucketCannotBeCreatedException;
    IResultData<BucketDataModel> updateBucket(String bucketUuid, BucketUpdateDataModel bucketUpdateDataModel) throws BucketNotFoundException;
    IResultData<Boolean> deleteBucketByUuid(String bucketUuid) throws BucketNotFoundException;
}
