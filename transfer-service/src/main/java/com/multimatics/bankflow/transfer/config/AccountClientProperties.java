package com.multimatics.bankflow.transfer.config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import java.time.Duration;
@ConfigurationProperties(prefix="bankflow.clients.account")
public record AccountClientProperties(
        Duration connectTimeout,
        Duration readTimeout,
        long labDelayMs) { }