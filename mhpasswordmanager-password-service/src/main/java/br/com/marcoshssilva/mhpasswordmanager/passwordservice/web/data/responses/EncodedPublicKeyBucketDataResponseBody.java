package br.com.marcoshssilva.mhpasswordmanager.passwordservice.web.data.responses;

@lombok.Getter
@lombok.Builder
public class EncodedPublicKeyBucketDataResponseBody {
    private final String bucketUuid;
    private final String base64EncodedPublicKey;
}
