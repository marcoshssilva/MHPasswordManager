package br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.client;

import br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.client.entities.AbstractKeyPayloadDecoded;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.client.entities.AbstractPasswordStoredValueDecoded;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.client.entities.AbstractSecurityQuestionStoredValueDecoded;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.client.entities.AesCryptKeyPayload;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.client.entities.BucketDataPayload;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.client.entities.CreateBucketPayload;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.client.entities.DecryptKeyBase64Payload;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.client.entities.KeyPayloadEncoded;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.client.entities.KeyStorePayloadEncoded;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.client.entities.PagePayload;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.client.entities.RsaCryptKeyPayload;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.client.entities.SimpleBucketCryptKeyPayload;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.client.entities.UpdateBucketPayload;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.client.entities.UserRegisteredPayload;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

@Component
public class PasswordServiceClient extends AbstractClient {

    public PasswordServiceClient(@LoadBalanced WebClient.Builder webClientBuilder) {
        super(webClientBuilder, "http://PASSWORD-SERVICE/mypass-manager/passwords");
    }

    public Mono<BucketDataPayload> createBucket(String accessToken, CreateBucketPayload payload) {
        return getWebClient().post()
                .uri("/bucket/create")
                .headers(headers -> setBearerAuth(headers, accessToken))
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(BucketDataPayload.class);
    }

    public Mono<PagePayload<BucketDataPayload>> getAllBuckets(String accessToken, Pageable pageable) {
        return getWebClient().get()
                .uri(uriBuilder -> addPageable(uriBuilder.path("/bucket"), pageable).build())
                .headers(headers -> setBearerAuth(headers, accessToken))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<PagePayload<BucketDataPayload>>() {
                });
    }

    public Mono<BucketDataPayload> getBucket(String accessToken, String bucketUuid) {
        return getWebClient().get()
                .uri("/bucket/{bucketUuid}", bucketUuid)
                .headers(headers -> setBearerAuth(headers, accessToken))
                .retrieve()
                .bodyToMono(BucketDataPayload.class);
    }

    public Mono<Void> deleteBucket(String accessToken, String bucketUuid) {
        return getWebClient().delete()
                .uri("/bucket/{bucketUuid}", bucketUuid)
                .headers(headers -> setBearerAuth(headers, accessToken))
                .retrieve()
                .bodyToMono(Void.class);
    }

    public Mono<BucketDataPayload> updateBucket(String accessToken, String bucketUuid, UpdateBucketPayload payload) {
        return getWebClient().put()
                .uri("/bucket/{bucketUuid}", bucketUuid)
                .headers(headers -> setBearerAuth(headers, accessToken))
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(BucketDataPayload.class);
    }

    public Mono<DecryptKeyBase64Payload> decryptRsaDataAsBase64(String accessToken, RsaCryptKeyPayload payload) {
        return getWebClient().post()
                .uri("/crypt/rsa/decrypt/base64")
                .headers(headers -> setBearerAuth(headers, accessToken))
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(DecryptKeyBase64Payload.class);
    }

    public Mono<JsonNode> decryptRsaDataAsJson(String accessToken, RsaCryptKeyPayload payload) {
        return getWebClient().post()
                .uri("/crypt/rsa/decrypt/json")
                .headers(headers -> setBearerAuth(headers, accessToken))
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(JsonNode.class);
    }

    public Mono<DecryptKeyBase64Payload> decryptAesDataAsBase64(String accessToken, AesCryptKeyPayload payload) {
        return getWebClient().post()
                .uri("/crypt/aes/decrypt/base64")
                .headers(headers -> setBearerAuth(headers, accessToken))
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(DecryptKeyBase64Payload.class);
    }

    public Mono<JsonNode> decryptAesDataAsJson(String accessToken, AesCryptKeyPayload payload) {
        return getWebClient().post()
                .uri("/crypt/aes/decrypt/json")
                .headers(headers -> setBearerAuth(headers, accessToken))
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(JsonNode.class);
    }

    public Mono<String> encryptDataInAesAndConvertAsBase64(String accessToken, AesCryptKeyPayload payload) {
        return getWebClient().post()
                .uri("/crypt/aes/encrypt/base64")
                .headers(headers -> setBearerAuth(headers, accessToken))
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<String> encryptDataInRsaAndConvertAsBase64(String accessToken, RsaCryptKeyPayload payload) {
        return getWebClient().post()
                .uri("/crypt/rsa/encrypt/base64")
                .headers(headers -> setBearerAuth(headers, accessToken))
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<PagePayload<KeyPayloadEncoded>> getAllKeys(String accessToken, String bucketUuid, Pageable pageable) {
        return getWebClient().get()
                .uri(uriBuilder -> addPageable(uriBuilder.path("/keys/{bucketUuid}/key/all"), pageable).build(bucketUuid))
                .headers(headers -> setBearerAuth(headers, accessToken))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<PagePayload<KeyPayloadEncoded>>() {
                });
    }

    public Mono<DecryptKeyBase64Payload> encryptBase64UsingBucket(String accessToken, String bucketUuid, SimpleBucketCryptKeyPayload payload) {
        return getWebClient().post()
                .uri("/keys/{bucketUuid}/encrypt/base64", bucketUuid)
                .headers(headers -> setBearerAuth(headers, accessToken))
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(DecryptKeyBase64Payload.class);
    }

    public Mono<DecryptKeyBase64Payload> encryptFileUsingBucket(String accessToken, String bucketUuid, MultipartFile file) {
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("file", file.getResource())
                .filename(file.getOriginalFilename());
        MultiValueMap<String, HttpEntity<?>> multipartBody = bodyBuilder.build();

        return getWebClient().post()
                .uri("/keys/{bucketUuid}/encrypt/file", bucketUuid)
                .headers(headers -> setBearerAuth(headers, accessToken))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(multipartBody))
                .retrieve()
                .bodyToMono(DecryptKeyBase64Payload.class);
    }

    public Mono<KeyPayloadEncoded> getKey(String accessToken, String bucketUuid, Long id) {
        return getWebClient().get()
                .uri("/keys/{bucketUuid}/key/{id}", bucketUuid, id)
                .headers(headers -> setBearerAuth(headers, accessToken))
                .retrieve()
                .bodyToMono(KeyPayloadEncoded.class);
    }

    public Mono<KeyPayloadEncoded> saveKey(String accessToken, String bucketUuid, AbstractKeyPayloadDecoded payload) {
        return getWebClient().post()
                .uri("/keys/{bucketUuid}/key/new", bucketUuid)
                .headers(headers -> setBearerAuth(headers, accessToken))
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(KeyPayloadEncoded.class);
    }

    public Mono<KeyPayloadEncoded> updateKey(String accessToken, String bucketUuid, Long id, AbstractKeyPayloadDecoded payload) {
        return getWebClient().put()
                .uri("/keys/{bucketUuid}/key/{id}", bucketUuid, id)
                .headers(headers -> setBearerAuth(headers, accessToken))
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(KeyPayloadEncoded.class);
    }

    public Mono<Void> deleteKey(String accessToken, String bucketUuid, Long id) {
        return getWebClient().delete()
                .uri("/keys/{bucketUuid}/key/{id}", bucketUuid, id)
                .headers(headers -> setBearerAuth(headers, accessToken))
                .retrieve()
                .bodyToMono(Void.class);
    }

    public Mono<KeyStorePayloadEncoded> getKeyPayload(String accessToken, String bucketUuid, Long id, Long keyStoreId) {
        return getWebClient().get()
                .uri("/keys/{bucketUuid}/key/{id}/stored/{keyStoreId}", bucketUuid, id, keyStoreId)
                .headers(headers -> setBearerAuth(headers, accessToken))
                .retrieve()
                .bodyToMono(KeyStorePayloadEncoded.class);
    }

    public Mono<KeyStorePayloadEncoded> saveKeyPayloadPassword(String accessToken, String bucketUuid, Long id, AbstractPasswordStoredValueDecoded payload) {
        return getWebClient().post()
                .uri("/keys/{bucketUuid}/key/{id}/stored/new/password", bucketUuid, id)
                .headers(headers -> setBearerAuth(headers, accessToken))
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(KeyStorePayloadEncoded.class);
    }

    public Mono<KeyStorePayloadEncoded> updateKeyPayloadPassword(String accessToken, String bucketUuid, Long keyId, Long keyStoredId, AbstractPasswordStoredValueDecoded payload) {
        return getWebClient().put()
                .uri("/keys/{bucketUuid}/key/{keyId}/stored/{keyStoredId}/password", bucketUuid, keyId, keyStoredId)
                .headers(headers -> setBearerAuth(headers, accessToken))
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(KeyStorePayloadEncoded.class);
    }

    public Mono<KeyStorePayloadEncoded> saveKeyPayloadSecurityQuestion(String accessToken, String bucketUuid, Long id, AbstractSecurityQuestionStoredValueDecoded payload) {
        return getWebClient().post()
                .uri("/keys/{bucketUuid}/key/{id}/stored/new/security-question", bucketUuid, id)
                .headers(headers -> setBearerAuth(headers, accessToken))
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(KeyStorePayloadEncoded.class);
    }

    public Mono<KeyStorePayloadEncoded> updateKeyPayloadSecurityQuestion(String accessToken, String bucketUuid, Long keyId, Long keyStoredId, AbstractSecurityQuestionStoredValueDecoded payload) {
        return getWebClient().put()
                .uri("/keys/{bucketUuid}/key/{keyId}/stored/{keyStoredId}/security-question", bucketUuid, keyId, keyStoredId)
                .headers(headers -> setBearerAuth(headers, accessToken))
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(KeyStorePayloadEncoded.class);
    }

    public Mono<Void> deleteKeyPayload(String accessToken, String bucketUuid, Long keyId, Long keyStoredId) {
        return getWebClient().delete()
                .uri("/keys/{bucketUuid}/key/{keyId}/stored/{keyStoredId}", bucketUuid, keyId, keyStoredId)
                .headers(headers -> setBearerAuth(headers, accessToken))
                .retrieve()
                .bodyToMono(Void.class);
    }

    public Mono<UserRegisteredPayload> registrationData(String accessToken) {
        return getWebClient().get()
                .uri("/account/data")
                .headers(headers -> setBearerAuth(headers, accessToken))
                .retrieve()
                .bodyToMono(UserRegisteredPayload.class);
    }

    private void setBearerAuth(HttpHeaders headers, String accessToken) {
        if (accessToken == null || accessToken.isBlank()) {
            return;
        }
        String tokenValue = accessToken.startsWith("Bearer ") ? accessToken.substring("Bearer ".length()) : accessToken;
        headers.setBearerAuth(tokenValue);
    }

    private UriBuilder addPageable(UriBuilder uriBuilder, Pageable pageable) {
        if (pageable == null) {
            return uriBuilder;
        }

        uriBuilder.queryParam("page", pageable.getPageNumber());
        uriBuilder.queryParam("size", pageable.getPageSize());
        pageable.getSort().forEach(order -> uriBuilder.queryParam("sort", order.getProperty() + "," + order.getDirection().name()));
        return uriBuilder;
    }

}
