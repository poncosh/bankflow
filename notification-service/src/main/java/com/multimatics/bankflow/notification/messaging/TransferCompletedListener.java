package com.multimatics.bankflow.notification.messaging;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.*;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import com.multimatics.bankflow.notification.reliability.*;

@Component
public class TransferCompletedListener {
 private static final Logger log = LoggerFactory.getLogger(TransferCompletedListener.class);
 private final ReliableNotificationProcessor processor;
 public TransferCompletedListener(ReliableNotificationProcessor processor) {
  this.processor = processor;
 }

 @KafkaListener(topics = "${bankflow.kafka.transfer-completed-topic}")
 public void onTransferCompleted(ConsumerRecord<String,TransferCompletedEvent> record){
  var event = record.value();
  log.info("record_received eventId={} key={} partition={} offset={}",
         event.eventId(), record.key(), record.partition(), record.offset());
  processor.process(event);
 }
}
