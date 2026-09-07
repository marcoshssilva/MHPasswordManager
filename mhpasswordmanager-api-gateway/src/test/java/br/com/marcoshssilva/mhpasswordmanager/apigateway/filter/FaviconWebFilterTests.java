package br.com.marcoshssilva.mhpasswordmanager.apigateway.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FaviconWebFilterTests {

    private static final String DEFAULT_RESPONSE = "DEFAULT RESPONSE";

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        this.webTestClient = WebTestClient
                .bindToRouterFunction(RouterFunctions.route()
                        .GET("/**", req -> ServerResponse.ok().bodyValue(DEFAULT_RESPONSE)).build())
                .webFilter(new FaviconWebFilter()).build();
    }

    @DisplayName("Should return favicon.ico with OK status and correct headers when GET /favicon.ico")
    @Test
    void shouldReturnFaviconWhenRequested() {
        webTestClient.get().uri("/favicon.ico").exchange().expectStatus().isOk().expectHeader()
                .valueEquals(HttpHeaders.CONTENT_TYPE, "image/x-icon").expectHeader().exists(HttpHeaders.CACHE_CONTROL)
                .expectBody(byte[].class).consumeWith(result -> {
                    assertNotNull(result.getResponseBody());
                    assertTrue(result.getResponseBody().length > 0);
                });
    }

    @DisplayName("Should pass through filter and not return favicon.ico for any other path matching /**")
    @ParameterizedTest
    @ValueSource(strings = { "/", "/home", "/api/v1/resource", "/favicon.ico/subpath", "/static/favicon.ico",
            "/index.html" })
    void shouldNotReturnFaviconForOtherPaths(String path) {
        webTestClient.get().uri(path).exchange().expectStatus().isOk().expectHeader()
                .valueMatches(HttpHeaders.CONTENT_TYPE, "(?!image/x-icon).*").expectBody(String.class)
                .isEqualTo(DEFAULT_RESPONSE);
    }
}
