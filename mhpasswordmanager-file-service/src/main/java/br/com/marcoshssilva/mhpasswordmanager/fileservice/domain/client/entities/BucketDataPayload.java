package br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.client.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class BucketDataPayload {
    private String bucketUuid;
    private String bucketName;
    private String bucketDescription;
    private String encodedPublicKey;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    private Date createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    private Date lastUpdate;
}
