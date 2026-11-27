package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.models;

@lombok.Getter
@lombok.AllArgsConstructor
@lombok.EqualsAndHashCode
@lombok.ToString
public final class BucketPrivateKeyExportModel {
    private final String bucketUuid;

    @lombok.ToString.Exclude
    private final String base64EncodedPrivateKey;

    private final String algoritm;
}
