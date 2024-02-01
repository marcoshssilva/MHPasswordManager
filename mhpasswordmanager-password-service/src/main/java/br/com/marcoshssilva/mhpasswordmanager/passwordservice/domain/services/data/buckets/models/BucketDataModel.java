package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.models;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities.UserBucket;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@lombok.NoArgsConstructor
@lombok.Getter
@lombok.Setter
@lombok.AllArgsConstructor
@lombok.Builder
@lombok.ToString
public class BucketDataModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String bucketUuid;
    private String bucketName;
    private String bucketDescription;
    private Date createdAt;
    private Date lastUpdate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BucketDataModel that = (BucketDataModel) o;
        return Objects.equals(bucketUuid, that.bucketUuid)
                && Objects.equals(bucketName, that.bucketName)
                && Objects.equals(bucketDescription, that.bucketDescription)
                && Objects.equals(createdAt, that.createdAt)
                && Objects.equals(lastUpdate, that.lastUpdate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bucketUuid, bucketName, bucketDescription, createdAt, lastUpdate);
    }

    public static BucketDataModel fromEntity(UserBucket userBucket) {
        return BucketDataModel.builder()
                .bucketUuid(userBucket.getId())
                .bucketName(userBucket.getName())
                .bucketDescription(userBucket.getDescription())
                .createdAt(userBucket.getCreatedAt())
                .lastUpdate(userBucket.getLastUpdate())
                .build();
    }
}
