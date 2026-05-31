package br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.client;

import org.springframework.web.reactive.function.client.WebClient;

public abstract class AbstractClient {
    private final WebClient webClient;

    protected AbstractClient(WebClient.Builder webClientBuilder, String baseUrl) {
        this.webClient = createWebClient(webClientBuilder, baseUrl);
    }

    private WebClient createWebClient(WebClient.Builder webClientBuilder, String baseUrl) {
        return webClientBuilder.baseUrl(baseUrl).build();
    }

    protected WebClient getWebClient() {
        return webClient;
    }
}
