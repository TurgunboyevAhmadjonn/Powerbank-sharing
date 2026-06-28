// src/main/java/com/anor/rental/domain/entity/Rental.java
package com.anor.rental.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "rentals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "station_id", nullable = false)
    private UUID stationId;

    @Column(name = "return_station_id")
    private UUID returnStationId;

    @Column(name = "slot_id", nullable = false)
    private UUID slotId;

    @Column(name = "return_slot_id")
    private UUID returnSlotId;

    @Column(name = "powerbank_id")
    private UUID powerbankId;

    @Column(name = "card_id", nullable = false)
    private UUID cardId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RentalStatus status;

    @Column(name = "started_at")
    private OffsetDateTime startedAt;

    @Column(name = "finished_at")
    private OffsetDateTime finishedAt;

    @Column(name = "total_cost", precision = 12, scale = 2)
    private BigDecimal totalCost;

    @Column(name = "rate_per_hour", nullable = false, precision = 8, scale = 2)
    private BigDecimal ratePerHour;

    @Column(name = "last_billed_at")
    private OffsetDateTime lastBilledAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
