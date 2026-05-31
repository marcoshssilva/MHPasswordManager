package br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.client;

import br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.client.entities.DecryptKeyBase64Payload;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class PasswordServiceClient {
    private static final String DEFAULT_UPLOAD_FILENAME = "uploaded-file";
    private final WebClient webClient;

    public PasswordServiceClient(@LoadBalanced WebClient.Builder webClientBuilder) {
        this.webClient = createWebClient(webClientBuilder, "http://PASSWORD-SERVICE/mypass-manager/passwords");
    }

    private WebClient createWebClient(WebClient.Builder webClientBuilder, String baseUrl) {
        return webClientBuilder.baseUrl(baseUrl).build();
    }

    public WebClient getWebClient() {
        return webClient;
    }

    public Mono<DecryptKeyBase64Payload> encryptFileUsingBucket(String accessToken, String bucketUuid, MultipartFile file) {
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("file", file.getResource()).filename(getUploadFilename(file));
        MultiValueMap<String, HttpEntity<?>> multipartBody = bodyBuilder.build();

        return getWebClient().post()
                .uri("/keys/{bucketUuid}/encrypt/file", bucketUuid)
                .headers(headers -> setBearerAuth(headers, accessToken))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(multipartBody))
                .retrieve()
                .bodyToMono(DecryptKeyBase64Payload.class);
    }

    private String getUploadFilename(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            return DEFAULT_UPLOAD_FILENAME;
        }
        return originalFilename;
    }

    private void setBearerAuth(HttpHeaders headers, String accessToken) {
        if (accessToken == null || accessToken.isBlank()) {
            return;
        }
        String tokenValue = accessToken.startsWith("Bearer ") ? accessToken.substring("Bearer ".length()) : accessToken;
        headers.setBearerAuth(tokenValue);
    }
}
