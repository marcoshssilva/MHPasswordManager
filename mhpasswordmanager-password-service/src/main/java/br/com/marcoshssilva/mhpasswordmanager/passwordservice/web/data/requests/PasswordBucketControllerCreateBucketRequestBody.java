package br.com.marcoshssilva.mhpasswordmanager.passwordservice.web.data.requests;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@lombok.ToString
@lombok.NoArgsConstructor
@lombok.Getter
@lombok.Setter
public class PasswordBucketControllerCreateBucketRequestBody {
    @NotBlank
    @Size(min = 8, max = 40)
    private String bucketName;

    @NotBlank
    @Size(max = 255)
    private String bucketDescription;

    @NotBlank
    @Size(min = 10, max = 36)
    private String bucketSecret;
}
