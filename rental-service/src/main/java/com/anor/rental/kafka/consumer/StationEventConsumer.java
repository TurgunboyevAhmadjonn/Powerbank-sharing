// src/main/java/com/anor/rental/kafka/consumer/StationEventConsumer.java
package com.anor.rental.kafka.consumer;

import com.anor.rental.kafka.event.StationResponseEvent;
import com.anor.rental.service.RentalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StationEventConsumer {

    private final RentalService rentalService;

    @KafkaListener(
        topics = "${kafka.topics.station-events}",
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(@Payload StationResponseEvent event, Acknowledgment ack) {
        log.info("Received station event: type={} rentalId={}",
                event.getEventType(), event.getRentalId());
        try {
            switch (event.getEventType()) {
                case POWERBANK_EJECTED  -> rentalService.onPowerbankEjected(event);
                case POWERBANK_RETURNED -> rentalService.onPowerbankReturned(event);
                case EJECTION_FAILED    -> rentalService.onEjectionFailed(event);
            }
            ack.acknowledge();
        } catch (Exception e) {
            // Do NOT ack — Kafka will redeliver.
            // After max retries the message goes to a DLT (configure separately).
            log.error("Error processing station event rentalId={}: {}",
                    event.getRentalId(), e.getMessage(), e);
        }
    }
}
