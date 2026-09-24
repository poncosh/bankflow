package com.multimatics.bankflow.notification.reliability;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
@Component
public class InMemoryProcessedEventStore implements ProcessedEventStore {
    private final Set<String> processed = ConcurrentHashMap.newKeySet();
    private String key(String consumerName, UUID eventId) {
        return consumerName + ":" + eventId;
    }
    @Override
    public boolean isProcessed(String consumerName, UUID eventId) {
        return processed.contains(key(consumerName, eventId));
    }
    @Override
    public boolean markProcessed(String consumerName, UUID eventId) {
        return processed.add(key(consumerName, eventId));
    }
    @Override
    public int size() { return processed.size(); }
}