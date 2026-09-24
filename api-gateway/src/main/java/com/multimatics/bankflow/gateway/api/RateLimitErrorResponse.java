package com.multimatics.bankflow.gateway.api;

public record RateLimitErrorResponse(
        int status,
        String code,
        String message) {
}
