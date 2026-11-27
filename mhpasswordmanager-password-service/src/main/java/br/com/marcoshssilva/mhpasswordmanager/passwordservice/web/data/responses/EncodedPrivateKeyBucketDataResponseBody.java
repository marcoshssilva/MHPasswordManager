package br.com.marcoshssilva.mhpasswordmanager.passwordservice.web.data.responses;

@lombok.Getter
@lombok.Builder
public class EncodedPrivateKeyBucketDataResponseBody {
    private final String bucketUuid;
    private final String base64EncodedPrivateKey;
    private final String algoritm;
}
