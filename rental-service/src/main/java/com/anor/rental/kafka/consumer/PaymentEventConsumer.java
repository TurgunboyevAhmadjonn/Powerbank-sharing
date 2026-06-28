// src/main/java/com/anor/rental/kafka/consumer/PaymentEventConsumer.java
package com.anor.rental.kafka.consumer;

import com.anor.rental.kafka.event.PaymentResultEvent;
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
public class PaymentEventConsumer {

    private final RentalService rentalService;

    @KafkaListener(
        topics = "${kafka.topics.payment-events}",
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(@Payload PaymentResultEvent event, Acknowledgment ack) {
        log.info("Received payment event: status={} rentalId={} chargeId={}",
                event.getStatus(), event.getRentalId(), event.getChargeId());
        try {
            rentalService.onPaymentResult(event);
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error processing payment event rentalId={}: {}",
                    event.getRentalId(), e.getMessage(), e);
        }
    }
}
