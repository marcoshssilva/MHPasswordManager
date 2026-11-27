package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.models;

public record BucketPrivateKeyExport(String bucketUuid, String base64EncodedPrivateKey, String algoritm) {
    @Override
    public String toString() {
        return "BucketPrivateKeyExport[bucketUuid=" + bucketUuid + ", algoritm=" + algoritm + "]";
    }
}
