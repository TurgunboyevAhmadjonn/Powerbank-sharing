package com.anor.paymentservice.kafka;

import com.anor.paymentservice.model.Payment;
import com.anor.paymentservice.dto.PaymentRequestEvent;
import com.anor.paymentservice.dto.PaymentResultEvent;
import com.anor.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentEventListener {

    private final PaymentService paymentService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "payment-requests", groupId = "payment-group")
    public void handlePaymentRequest(PaymentRequestEvent event) {
        try {
            Payment result = paymentService.processPayment(
                    event.getIdempotencyKey(),
                    event.getCardId(),
                    event.getAmount()
            );

            PaymentResultEvent resultEvent = new PaymentResultEvent(
                    result.getId(),
                    result.getIdempotencyKey(),
                    result.getStatus().name()
            );
            
            kafkaTemplate.send("payment-events", result.getIdempotencyKey(), resultEvent);

        } catch (Exception e) {
            // Handle fatal errors
        }
    }
}