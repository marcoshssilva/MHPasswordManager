package br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.client;

import br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.client.entities.DecryptKeyBase64Payload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordServiceClientTests {

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    @SuppressWarnings("rawtypes")
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    @SuppressWarnings("rawtypes")
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    @SuppressWarnings("rawtypes")
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    private PasswordServiceClient passwordServiceClient;

    @BeforeEach
    void setUp() {
        when(webClientBuilder.baseUrl(anyString())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);

        passwordServiceClient = new PasswordServiceClient(webClientBuilder);
    }

    @DisplayName("Should initialize webClient properly")
    @Test
    void shouldInitializeWebClient() {
        assertThat(passwordServiceClient.getWebClient()).isEqualTo(webClient);
    }

    @DisplayName("Should encrypt file using bucket endpoint and return DecryptKeyBase64Payload")
    @Test
    @SuppressWarnings("unchecked")
    void shouldEncryptFileUsingBucket() {
        MockMultipartFile file = new MockMultipartFile("file", "document.pdf", MediaType.APPLICATION_PDF_VALUE, "payload".getBytes());
        DecryptKeyBase64Payload expectedPayload = new DecryptKeyBase64Payload();
        expectedPayload.setData("base64-data");

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(eq("/keys/{bucketUuid}/encrypt/file"), eq("bucket-uuid"))).thenReturn(requestBodySpec);
        when(requestBodySpec.headers(any(Consumer.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(MediaType.MULTIPART_FORM_DATA)).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(BodyInserter.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(DecryptKeyBase64Payload.class)).thenReturn(Mono.just(expectedPayload));

        Mono<DecryptKeyBase64Payload> resultMono = passwordServiceClient.encryptFileUsingBucket("Bearer sample-token", "bucket-uuid", file);

        DecryptKeyBase64Payload result = resultMono.block();
        assertThat(result).isEqualTo(expectedPayload);
    }

    @DisplayName("Should encrypt file with default filename if original filename is empty")
    @Test
    @SuppressWarnings("unchecked")
    void shouldEncryptFileWithDefaultFilename() {
        MockMultipartFile file = new MockMultipartFile("file", "", MediaType.APPLICATION_PDF_VALUE, "payload".getBytes());
        DecryptKeyBase64Payload expectedPayload = new DecryptKeyBase64Payload();
        expectedPayload.setData("base64-data");

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(eq("/keys/{bucketUuid}/encrypt/file"), eq("bucket-uuid"))).thenReturn(requestBodySpec);
        when(requestBodySpec.headers(any(Consumer.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(MediaType.MULTIPART_FORM_DATA)).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(BodyInserter.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(DecryptKeyBase64Payload.class)).thenReturn(Mono.just(expectedPayload));

        Mono<DecryptKeyBase64Payload> resultMono = passwordServiceClient.encryptFileUsingBucket(null, "bucket-uuid", file);

        DecryptKeyBase64Payload result = resultMono.block();
        assertThat(result).isEqualTo(expectedPayload);
    }
}
