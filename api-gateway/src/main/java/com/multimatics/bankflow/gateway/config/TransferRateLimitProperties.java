package com.multimatics.bankflow.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "bankflow.gateway.transfer-rate-limit")
public record TransferRateLimitProperties(
        String name,
        String path,
        String errorCode,
        String message) {
}
