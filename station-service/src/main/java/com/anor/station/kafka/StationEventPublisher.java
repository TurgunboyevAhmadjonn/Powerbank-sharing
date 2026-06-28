// kafka/StationEventPublisher.java
package com.anor.station.kafka;

import com.anor.station.event.StationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StationEventPublisher {

    private static final String TOPIC = "station-events";
    private final KafkaTemplate<String, StationEvent> kafkaTemplate;

    /**
     * Key = stationId so all events from the same station land on the same
     * partition and keep per-station ordering. See DECISIONS.md (Kafka Design).
     */
    public void publish(StationEvent event) {
        kafkaTemplate.send(TOPIC, event.stationId(), event)
            .whenComplete((res, ex) -> {
                if (ex != null) {
                    // MVP: log + rely on producer retries. Production answer
                    // is the Outbox pattern — documented in DECISIONS.md.
                    log.error("Failed to publish station event {}", event.eventId(), ex);
                } else {
                    log.info("Published {} for rental {}", event.type(), event.rentalId());
                }
            });
    }
}
