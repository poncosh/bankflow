package com.multimatics.bankflow.gateway;

import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TransferRateLimitFilterTest {

    private TransferRateLimitFilter filter;
    private GatewayFilterChain chain;

    @BeforeEach
    void setUp() {
        var config = RateLimiterConfig.custom()
                .limitForPeriod(5)
                .limitRefreshPeriod(Duration.ofSeconds(10))
                .timeoutDuration(Duration.ZERO)
                .build();
        filter = new TransferRateLimitFilter(RateLimiterRegistry.of(config));
        chain = mock(GatewayFilterChain.class);
        when(chain.filter(any())).thenReturn(Mono.empty());
    }

    @Test
    void allowsFiveTransferRequestsAndRejectsTheSixthWith429() {
        for (int request = 1; request <= 5; request++) {
            var exchange = exchange("/api/transfers");
            filter.filter(exchange, chain).block();
            assertNull(exchange.getResponse().getStatusCode());
        }

        var rejected = exchange("/api/transfers");
        filter.filter(rejected, chain).block();

        assertEquals(HttpStatus.TOO_MANY_REQUESTS, rejected.getResponse().getStatusCode());
        assertEquals("10", rejected.getResponse().getHeaders().getFirst(HttpHeaders.RETRY_AFTER));
        String body = rejected.getResponse().getBodyAsString().block();
        assertTrue(body.contains(TransferRateLimitFilter.ERROR_CODE));
        verify(chain, times(5)).filter(any());
    }

    @Test
    void doesNotRateLimitAccountRequests() {
        for (int request = 1; request <= 10; request++) {
            var exchange = exchange("/api/accounts");
            filter.filter(exchange, chain).block();
            assertNull(exchange.getResponse().getStatusCode());
        }

        verify(chain, times(10)).filter(any());
    }

    private MockServerWebExchange exchange(String path) {
        return MockServerWebExchange.from(MockServerHttpRequest.get(path).build());
    }
}
