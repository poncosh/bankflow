package com.multimatics.bankflow.notification.reliability;
import com.multimatics.bankflow.notification.messaging.TransferCompletedEvent;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
@Component
public class NotificationDeliverySimulator {
    private static final Logger log =
            LoggerFactory.getLogger(NotificationDeliverySimulator.class);
    private final ConcurrentHashMap<UUID, AtomicInteger> attempts =
            new ConcurrentHashMap<>();
    public void deliver(TransferCompletedEvent event) {
        int attempt = attempts
                .computeIfAbsent(event.eventId(), id -> new AtomicInteger())
                .incrementAndGet();
        log.info("delivery_attempt eventId={} attempt={}",
                event.eventId(), attempt);
        if ("SIMULATE-PERMANENT".equals(event.sourceAccount())) {
            throw new PermanentNotificationException(
                    "Synthetic permanent notification failure");
        }
        if ("SIMULATE-TRANSIENT".equals(event.sourceAccount())
                && attempt < 3) {
            throw new TransientNotificationException(
                    "Synthetic transient notification failure");
        }
        log.info("notification_sent eventId={} transferId={}",
                event.eventId(), event.transferId());
    }
}