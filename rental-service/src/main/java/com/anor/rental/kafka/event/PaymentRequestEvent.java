// src/main/java/com/anor/rental/kafka/event/PaymentRequestEvent.java
package com.anor.rental.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Sent TO payment-service to charge the user.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestEvent {

    public enum PaymentType {
        RENTAL_CHARGE,
        RECURRING_CHARGE,
        FINAL_CHARGE
    }

    private String      eventId;          // idempotency key for payment-service
    private String      rentalId;
    private String      userId;
    private String      cardId;
    private BigDecimal  amount;
    private PaymentType paymentType;
}
