package com.multimatics.bankflow.gateway;

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

import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Component
public class TransferRateLimitFilter implements GlobalFilter, Ordered {

    static final String RATE_LIMITER_NAME = "transferApi";
    static final String ERROR_CODE = "RATE_LIMIT_EXCEEDED";

    private static final byte[] RESPONSE_BODY = ("""
            {"status":429,"code":"RATE_LIMIT_EXCEEDED",\
            "message":"Transfer request rate limit exceeded"}
            """).replace("\n", "").getBytes(StandardCharsets.UTF_8);

    private final RateLimiter rateLimiter;
    private final String retryAfterSeconds;

    public TransferRateLimitFilter(RateLimiterRegistry rateLimiterRegistry) {
        this.rateLimiter = rateLimiterRegistry.rateLimiter(RATE_LIMITER_NAME);
        this.retryAfterSeconds = retryAfterSeconds(
                rateLimiter.getRateLimiterConfig().getLimitRefreshPeriod());
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
        DataBuffer body = response.bufferFactory().wrap(RESPONSE_BODY);
        return response.writeWith(Mono.just(body));
    }

    private boolean isTransferApi(ServerWebExchange exchange) {
        String path = exchange.getRequest().getPath().pathWithinApplication().value();
        return path.equals("/api/transfers") || path.startsWith("/api/transfers/");
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
