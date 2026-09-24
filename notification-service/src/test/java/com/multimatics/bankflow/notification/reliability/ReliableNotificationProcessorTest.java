package com.multimatics.bankflow.notification.reliability;

import static org.mockito.Mockito.*;

import com.multimatics.bankflow.notification.messaging.TransferCompletedEvent;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ReliableNotificationProcessorTest {
    private final ProcessedEventStore store =
            mock(ProcessedEventStore.class);
    private final NotificationDeliverySimulator delivery =
            mock(NotificationDeliverySimulator.class);
    private final ReliableNotificationProcessor processor =
            new ReliableNotificationProcessor(store, delivery);

    private TransferCompletedEvent event(UUID eventId) {
        return new TransferCompletedEvent(eventId, "TransferCompleted", 1,
                Instant.parse("2026-08-30T08:00:00Z"), UUID.randomUUID(),
                "100000000001", "100000000002", new BigDecimal("250000"),
                "IDR", "COMPLETED");
    }

    @Test
    void shouldSkipPreviouslyProcessedEvent() {
        UUID id = UUID.randomUUID();
        when(store.isProcessed(anyString(), eq(id))).thenReturn(true);
        processor.process(event(id));
        verifyNoInteractions(delivery);
        verify(store, never()).markProcessed(anyString(), any());
    }

    @Test
    void shouldMarkOnlyAfterSuccessfulDelivery() {
        UUID id = UUID.randomUUID();
        processor.process(event(id));
        var order = inOrder(delivery, store);
        order.verify(delivery).deliver(any());
        order.verify(store).markProcessed(anyString(), eq(id));
    }
}
