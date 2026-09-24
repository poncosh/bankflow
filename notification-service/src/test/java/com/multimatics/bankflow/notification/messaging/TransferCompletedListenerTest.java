package com.multimatics.bankflow.notification.messaging;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.multimatics.bankflow.notification.reliability.ReliableNotificationProcessor;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

@ExtendWith(OutputCaptureExtension.class)
class TransferCompletedListenerTest {

    @Test
    void logsAndProcessesTransferCompletedEvent(CapturedOutput output) {
        UUID eventId=UUID.randomUUID();
        UUID transferId=UUID.randomUUID();
        var event=new TransferCompletedEvent(
                eventId,
                "TransferCompleted",
                1,
                Instant.parse("2026-09-22T07:00:00Z"),
                transferId,
                "100000000001",
                "100000000002",
                new BigDecimal("50000.00"),
                "IDR",
                "COMPLETED");
        var record=new ConsumerRecord<>(
                "bankflow.transfer.completed.v1", 2, 15L, transferId.toString(), event);
        var processor=mock(ReliableNotificationProcessor.class);

        new TransferCompletedListener(processor).onTransferCompleted(record);

        verify(processor).process(event);
        assertTrue(output.getOut().contains("record_received eventId=" + eventId));
        assertTrue(output.getOut().contains("key=" + transferId));
        assertTrue(output.getOut().contains("partition=2"));
        assertTrue(output.getOut().contains("offset=15"));
    }
}
