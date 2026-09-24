package com.multimatics.bankflow.notification.reliability;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;
@Configuration
public class KafkaErrorHandlingConfiguration {
    @Bean
    DefaultErrorHandler kafkaErrorHandler(
            KafkaTemplate<Object, Object> kafkaTemplate) {
        var recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate);
        var handler = new DefaultErrorHandler(
                recoverer,
                new FixedBackOff(1_000L, 2L));
        handler.addNotRetryableExceptions(
                PermanentNotificationException.class,
                IllegalArgumentException.class);
        return handler;
    }
}