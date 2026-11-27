package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.models;

public record BucketPrivateKeyExportModel(String bucketUuid, String base64EncodedPrivateKey, String algoritm) {
    @Override
    public String toString() {
        return "BucketPrivateKeyExportModel[bucketUuid=" + bucketUuid + ", algoritm=" + algoritm + "]";
    }
}
