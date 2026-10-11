package br.com.marcoshssilva.mhpasswordmanager.apigateway.endpoints;

import br.com.marcoshssilva.mhpasswordmanager.apigateway.config.S3StorageProperties;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.util.concurrent.CompletionException;

@RestController
public class AvatarEndpointController {
    private final S3AsyncClient s3;
    private final S3StorageProperties configuration;

    public AvatarEndpointController(S3AsyncClient s3, S3StorageProperties configuration) {
        this.s3 = s3;
        this.configuration = configuration;
    }

    @RequestMapping("/mypass-manager/avatars/{filename}")
    public Mono<ResponseEntity<byte[]>> get(@PathVariable String filename, ServerHttpRequest request) {
        if (request.getMethod() != HttpMethod.GET) {
            return Mono.just(ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).allow(HttpMethod.GET).build());
        }

        if (!filename.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\\.png")) {
            return Mono.just(ResponseEntity.notFound().build());
        }

        if (configuration.getBucket().isBlank()) {
            return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build());
        }

        return Mono.defer(() -> Mono.fromFuture(s3.getObject(GetObjectRequest.builder().bucket(configuration.getBucket()).key("avatar/" + filename).build(), AsyncResponseTransformer.toBytes())))
                .subscribeOn(Schedulers.boundedElastic())
                .map(bytes -> ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(bytes.asByteArray()))
                .onErrorResume(AvatarEndpointController::monoOnResumeError);
    }

    private static Mono<? extends ResponseEntity<byte[]>> monoOnResumeError(Throwable error) {
        Throwable cause = error;
        while (cause instanceof CompletionException && cause.getCause() != null) {
            cause = cause.getCause();
        }

        HttpStatus status = HttpStatus.BAD_GATEWAY;

        if (cause instanceof S3Exception s3Error) {
            if (s3Error.statusCode() == 404) {
                status = HttpStatus.NOT_FOUND;
            } else if (s3Error.statusCode() >= 500 || s3Error.statusCode() == 429) {
                status = HttpStatus.SERVICE_UNAVAILABLE;
            }

        } else if (cause instanceof SdkClientException) {
            status = HttpStatus.SERVICE_UNAVAILABLE;
        }

        return Mono.just(ResponseEntity.status(status).build());
    }
}
