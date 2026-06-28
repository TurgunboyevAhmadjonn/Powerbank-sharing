// src/main/java/com/anor/rental/domain/entity/RecurringPaymentCharge.java
package com.anor.rental.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "recurring_payment_charges")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecurringPaymentCharge {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "rental_id", nullable = false)
    private UUID rentalId;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentChargeStatus status;

    @Column(name = "kafka_msg_id")
    private String kafkaMsgId;

    @Column(name = "charged_at", nullable = false)
    private OffsetDateTime chargedAt;

    public enum PaymentChargeStatus {
        PENDING, SUCCESS, FAILED
    }
}
