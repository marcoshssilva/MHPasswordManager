package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.exceptions.BucketNotFoundException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.models.BucketDataModel;

public interface UserBucketService {
    BucketDataModel getBucketByUuid(String bucketUuid) throws BucketNotFoundException;
}
