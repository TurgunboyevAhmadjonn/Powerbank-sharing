// src/main/java/com/anor/rental/domain/entity/RentalStatus.java
package com.anor.rental.domain.entity;

public enum RentalStatus {
    PENDING_STATION,   // Waiting for station to confirm powerbank ejection
    ACTIVE,            // Powerbank in user's hands, billing running
    PENDING_PAYMENT,   // Return confirmed, final charge in flight
    COMPLETED,         // All done
    CANCELLED,         // Cancelled before station responded
    FAILED             // Station or payment error
}
