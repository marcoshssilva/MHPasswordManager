package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.models;

import java.util.Objects;

@lombok.ToString
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Builder
@lombok.Getter
@lombok.Setter
public class BucketNewDataModel {
    private String bucketName;
    private String bucketDescription;
    private String bucketSecret;
    private String userOwner;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BucketNewDataModel that = (BucketNewDataModel) o;
        return Objects.equals(bucketName, that.bucketName) && Objects.equals(bucketDescription, that.bucketDescription) && Objects.equals(bucketSecret, that.bucketSecret) && Objects.equals(userOwner, that.userOwner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bucketName, bucketDescription, bucketSecret, userOwner);
    }
}
