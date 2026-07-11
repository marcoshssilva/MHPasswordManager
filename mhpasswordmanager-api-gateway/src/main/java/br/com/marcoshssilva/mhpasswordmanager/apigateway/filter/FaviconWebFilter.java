package br.com.marcoshssilva.mhpasswordmanager.apigateway.filter;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class FaviconWebFilter implements WebFilter {
    private static final Resource FAVICON = new ClassPathResource("static/favicon.ico");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest()
                .getPath()
                .pathWithinApplication()
                .value();

        if (!"/favicon.ico".equals(path)) {
            return chain.filter(exchange);
        }

        if (!FAVICON.exists() || !FAVICON.isReadable()) {
            exchange.getResponse().setStatusCode(HttpStatus.NOT_FOUND);
            return exchange.getResponse().setComplete();
        }

        var response = exchange.getResponse();

        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().set(HttpHeaders.CONTENT_TYPE, "image/x-icon");
        response.getHeaders().setCacheControl(CacheControl.maxAge(7, TimeUnit.DAYS).cachePublic().getHeaderValue());

        try {
            response.getHeaders().setContentLength(FAVICON.contentLength());
        } catch (IOException ignored) {
            // Content-Length é opcional.
        }

        return response.writeWith(DataBufferUtils.read(FAVICON, response.bufferFactory(), 4096));
    }
}