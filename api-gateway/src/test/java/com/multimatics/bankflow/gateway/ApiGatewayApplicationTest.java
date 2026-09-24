package com.multimatics.bankflow.gateway;

import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "eureka.client.enabled=false",
                "spring.cloud.discovery.enabled=false",
                "spring.cloud.service-registry.auto-registration.enabled=false",
                "spring.cloud.gateway.server.webflux.discovery.locator.enabled=false"
        })
class ApiGatewayApplicationTest {

    @Autowired
    private TransferRateLimitFilter transferRateLimitFilter;

    @Autowired
    private RateLimiterRegistry rateLimiterRegistry;

    @Test
    void configuresTransferApiRateLimiter() {
        assertNotNull(transferRateLimitFilter);
        var rateLimiter = rateLimiterRegistry.rateLimiter(
                TransferRateLimitFilter.RATE_LIMITER_NAME);
        assertEquals(5, rateLimiter.getRateLimiterConfig().getLimitForPeriod());
        assertEquals(0, rateLimiter.getRateLimiterConfig().getTimeoutDuration().toMillis());
    }
}
