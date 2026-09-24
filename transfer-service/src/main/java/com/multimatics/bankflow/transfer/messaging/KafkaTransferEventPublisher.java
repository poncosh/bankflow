package com.multimatics.bankflow.transfer.messaging;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
@Component
public class KafkaTransferEventPublisher implements TransferEventPublisher {
    private static final Logger log=LoggerFactory.getLogger(KafkaTransferEventPublisher.class);
    private final KafkaTemplate<String,TransferCompletedEvent> template;
    private final String topic;
    public KafkaTransferEventPublisher(
            KafkaTemplate<String,TransferCompletedEvent> template,
            @Value("#{environment['bankflow.kafka.topics.transfer-completed']}") String topic) {
        this.template=template; this.topic=topic;
    }
    public void publishTransferCompleted(TransferCompletedEvent event) {
        String key=event.transferId().toString();
        template.send(topic,key,event).whenComplete((result,error)->{
            if(error!=null){log.error("Publish failed eventId={}",event.eventId(),error);return;}
            var m=result.getRecordMetadata();
            log.info("Published eventId={} key={} partition={} offset={}",
                    event.eventId(),key,m.partition(),m.offset());
        });
    }
}
