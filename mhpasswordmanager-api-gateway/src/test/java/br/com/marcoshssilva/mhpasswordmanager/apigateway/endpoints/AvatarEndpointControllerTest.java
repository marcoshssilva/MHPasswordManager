package br.com.marcoshssilva.mhpasswordmanager.apigateway.endpoints;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "storage.s3.bucket=test-bucket",
        "management.server.port=0",
        "otel.sdk.disabled=true"
    }
)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class AvatarEndpointControllerTest {
    private static final String PATH = "/mypass-manager/avatars/12345678-1234-1234-1234-123456789abc.png";
    private static final String BUCKET_KEY = "avatar/12345678-1234-1234-1234-123456789abc.png";
    private static final String BUCKET_NAME = "test-bucket";

    @Autowired WebTestClient client;
    @MockBean S3AsyncClient s3;
    @MockBean ReactiveJwtDecoder jwtDecoder;

    @Test
    void publicGetReturnsPngBeforeCatchAllRedirect() {
        byte[] bytes = new byte[]{(byte)137, 80, 78, 71};

        when(s3.getObject(any(GetObjectRequest.class), org.mockito.ArgumentMatchers.<AsyncResponseTransformer<GetObjectResponse, ResponseBytes<GetObjectResponse>>>any()))
                .thenReturn(CompletableFuture.completedFuture(ResponseBytes.fromByteArray(GetObjectResponse.builder().build(), bytes)));

        client.get().uri(PATH).exchange().expectStatus().isOk()
                .expectHeader().contentType("image/png").expectHeader().doesNotExist("Location")
                .expectBody().consumeWith(result -> assertThat(result.getResponseBody()).isEqualTo(bytes));

        ArgumentCaptor<GetObjectRequest> requestCaptor = ArgumentCaptor.forClass(GetObjectRequest.class);

        verify(s3).getObject(requestCaptor.capture(), any(AsyncResponseTransformer.class));

        GetObjectRequest captured = requestCaptor.getValue();
        assertThat(captured.bucket()).isEqualTo(BUCKET_NAME);
        assertThat(captured.key()).isEqualTo(BUCKET_KEY);
    }

    @Test
    void invalidKeyDoesNotReachStorage() {
        client.get().uri("/mypass-manager/avatars/not-a-uuid.png").exchange().expectStatus().isNotFound();
        verifyNoInteractions(s3);
    }

    @Test
    void unsupportedMethodsAreRejectedEvenAnonymously() {
        client.post().uri(PATH).exchange().expectStatus().isEqualTo(405);
        client.head().uri(PATH).exchange().expectStatus().isEqualTo(405);
        client.delete().uri(PATH).exchange().expectStatus().isEqualTo(405);
        verifyNoInteractions(s3);
    }

    @ParameterizedTest
    @CsvSource({"404,404", "503,503", "403,502", "429,503"})
    void mapsStorageErrorsWithoutExposingDetails(int storageStatus, int httpStatus) {
        when(s3.getObject(any(GetObjectRequest.class), org.mockito.ArgumentMatchers.<AsyncResponseTransformer<GetObjectResponse, ResponseBytes<GetObjectResponse>>>any()))
                .thenReturn(CompletableFuture.failedFuture(S3Exception.builder().statusCode(storageStatus).message("internal bucket information").build()));

        client.get().uri(PATH).exchange().expectStatus().isEqualTo(httpStatus).expectBody().isEmpty();
    }
}
