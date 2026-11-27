package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.models;

@lombok.Getter
@lombok.AllArgsConstructor
@lombok.EqualsAndHashCode
public final class BucketPublicKeyExportModel {
    private final String bucketUuid;
    private final String base64EncodedPublicKey;
}
