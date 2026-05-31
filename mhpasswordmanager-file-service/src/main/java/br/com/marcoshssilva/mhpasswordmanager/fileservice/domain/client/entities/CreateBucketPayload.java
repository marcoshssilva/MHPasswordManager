package br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.client.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class CreateBucketPayload {
    private String bucketName;
    private String bucketDescription;
    private String bucketSecret;
}
