// src/main/java/com/anor/rental/kafka/producer/RentalEventProducer.java
package com.anor.rental.kafka.producer;

import com.anor.rental.kafka.event.PaymentRequestEvent;
import com.anor.rental.kafka.event.RentalCommandEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class RentalEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.rental-commands}")
    private String rentalCommandsTopic;

    @Value("${kafka.topics.payment-requests}")
    private String paymentRequestsTopic;

    /**
     * Key = stationId so all commands to the same station are ordered
     * on the same partition — important for sequential slot operations.
     */
    public void sendRentalCommand(RentalCommandEvent event) {
        String key = event.getStationId();
        send(rentalCommandsTopic, key, event);
    }

    /**
     * Key = rentalId to keep all payment events for a rental on one partition.
     */
    public void sendPaymentRequest(PaymentRequestEvent event) {
        String key = event.getRentalId();
        send(paymentRequestsTopic, key, event);
    }

    private void send(String topic, String key, Object payload) {
        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(topic, key, payload);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                // In production: write to an outbox table / dead-letter queue.
                // For MVP: log and let the caller handle retries.
                log.error("Failed to send Kafka message to topic={} key={}: {}",
                        topic, key, ex.getMessage(), ex);
            } else {
                log.debug("Kafka message sent topic={} key={} offset={}",
                        topic, key, result.getRecordMetadata().offset());
            }
        });
    }
}
