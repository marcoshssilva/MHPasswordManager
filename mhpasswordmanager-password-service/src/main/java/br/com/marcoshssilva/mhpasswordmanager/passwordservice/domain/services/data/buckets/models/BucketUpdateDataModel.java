package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.models;

import java.util.Objects;

@lombok.ToString
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Builder
@lombok.Getter
@lombok.Setter
public class BucketUpdateDataModel {
    private String bucketName;
    private String bucketDescription;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BucketUpdateDataModel that = (BucketUpdateDataModel) o;
        return Objects.equals(bucketName, that.bucketName) && Objects.equals(bucketDescription, that.bucketDescription);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bucketName, bucketDescription);
    }
}
