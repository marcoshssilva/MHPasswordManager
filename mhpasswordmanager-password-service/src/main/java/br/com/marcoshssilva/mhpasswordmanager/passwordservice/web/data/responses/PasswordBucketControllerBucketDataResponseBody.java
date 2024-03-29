package br.com.marcoshssilva.mhpasswordmanager.passwordservice.web.data.responses;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

@lombok.Getter
@lombok.Setter
@lombok.NoArgsConstructor
@lombok.ToString
@lombok.AllArgsConstructor
@lombok.Builder
public class PasswordBucketControllerBucketDataResponseBody {

    private String bucketUuid;
    private String bucketName;
    private String bucketDescription;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    private Date createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    private Date lastUpdate;
}
