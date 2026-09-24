package com.multimatics.bankflow.notification.reliability;
import com.multimatics.bankflow.notification.messaging.TransferCompletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
@Service
public class ReliableNotificationProcessor {
    public static final String CONSUMER_NAME =
            "bankflow-notification-service";
    private static final Logger log =
            LoggerFactory.getLogger(ReliableNotificationProcessor.class);
    private final ProcessedEventStore store;
    private final NotificationDeliverySimulator delivery;
    public ReliableNotificationProcessor(
            ProcessedEventStore store,
            NotificationDeliverySimulator delivery) {
        this.store = store;
        this.delivery = delivery;
    }
    public void process(TransferCompletedEvent event) {
        if (store.isProcessed(CONSUMER_NAME, event.eventId())) {
            log.info("duplicate_skipped eventId={} transferId={}",
                    event.eventId(), event.transferId());
            return;
        }
        delivery.deliver(event);
        store.markProcessed(CONSUMER_NAME, event.eventId());
        log.info("processed eventId={} transferId={} storeSize={}",
                event.eventId(), event.transferId(), store.size());
    }
}