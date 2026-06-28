// src/main/java/com/anor/rental/kafka/event/PaymentResultEvent.java
package com.anor.rental.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Received FROM payment-service.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResultEvent {

    public enum PaymentStatus {
        SUCCESS,
        FAILED,
        CANCELLED
    }

    private String        eventId;
    private String        rentalId;
    private String        chargeId;       // recurring_payment_charges.id
    private BigDecimal    amount;
    private PaymentStatus status;
    private String        failureReason;
}

