package com.multimatics.bankflow.gateway;

import com.multimatics.bankflow.gateway.api.RateLimitErrorResponse;
import com.multimatics.bankflow.gateway.config.TransferRateLimitProperties;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;

@Component
public class TransferRateLimitFilter implements GlobalFilter, Ordered {

    private final RateLimiter rateLimiter;
    private final String requestPath;
    private final String retryAfterSeconds;
    private final byte[] responseBody;

    public TransferRateLimitFilter(
            RateLimiterRegistry rateLimiterRegistry,
            TransferRateLimitProperties properties,
            ObjectMapper objectMapper) {
        this.rateLimiter = rateLimiterRegistry.rateLimiter(properties.name());
        this.requestPath = properties.path();
        this.retryAfterSeconds = retryAfterSeconds(
                rateLimiter.getRateLimiterConfig().getLimitRefreshPeriod());
        this.responseBody = serializeResponse(objectMapper, properties);
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!isTransferApi(exchange)) {
            return chain.filter(exchange);
        }
        if (rateLimiter.acquirePermission()) {
            return chain.filter(exchange);
        }

        var response = exchange.getResponse();
        response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        response.getHeaders().set(HttpHeaders.RETRY_AFTER, retryAfterSeconds);
        DataBuffer body = response.bufferFactory().wrap(responseBody);
        return response.writeWith(Mono.just(body));
    }

    private boolean isTransferApi(ServerWebExchange exchange) {
        String path = exchange.getRequest().getPath().pathWithinApplication().value();
        return path.equals(requestPath) || path.startsWith(requestPath + "/");
    }

    private byte[] serializeResponse(
            ObjectMapper objectMapper,
            TransferRateLimitProperties properties) {
        var response = new RateLimitErrorResponse(
                HttpStatus.TOO_MANY_REQUESTS.value(),
                properties.errorCode(),
                properties.message());
        try {
            return objectMapper.writeValueAsBytes(response);
        } catch (JacksonException exception) {
            throw new IllegalStateException(
                    "Cannot serialize transfer rate limit response", exception);
        }
    }

    private String retryAfterSeconds(Duration refreshPeriod) {
        long seconds = Math.max(1, (refreshPeriod.toMillis() + 999) / 1_000);
        return Long.toString(seconds);
    }

    @Override
    public int getOrder() {
        return -50;
    }
}
