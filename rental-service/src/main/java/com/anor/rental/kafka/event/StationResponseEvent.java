// src/main/java/com/anor/rental/kafka/event/StationResponseEvent.java
package com.anor.rental.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Received FROM station-service.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StationResponseEvent {

    public enum EventType {
        POWERBANK_EJECTED,
        POWERBANK_RETURNED,
        EJECTION_FAILED
    }

    private String    eventId;
    private String    rentalId;
    private String    powerbankId;
    private String    stationId;
    private String    slotId;
    private EventType eventType;
    private String    errorMessage;
}
