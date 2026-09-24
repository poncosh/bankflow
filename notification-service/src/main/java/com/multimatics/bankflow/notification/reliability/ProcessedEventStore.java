package com.multimatics.bankflow.notification.reliability;
import java.util.UUID;
public interface ProcessedEventStore {
    boolean isProcessed(String consumerName, UUID eventId);
    boolean markProcessed(String consumerName, UUID eventId);
    int size();
}