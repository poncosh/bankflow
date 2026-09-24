package com.multimatics.bankflow.notification.messaging;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransferCompletedEvent(
        UUID eventId,
        String eventType,
        int eventVersion,
        Instant occurredAt,
        UUID transferId,
        String sourceAccount,
        String destinationAccount,
        BigDecimal amount,
        String currency,
        String status
) {}
