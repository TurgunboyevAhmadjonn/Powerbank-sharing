// src/main/java/com/anor/rental/kafka/event/RentalCommandEvent.java
package com.anor.rental.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Sent TO station-service to request powerbank ejection or accept return.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RentalCommandEvent {

    public enum CommandType {
        EJECT_POWERBANK,
        ACCEPT_RETURN
    }

    private String    eventId;     // UUID — used as Kafka key
    private String    rentalId;
    private String    stationId;
    private String    slotId;
    private CommandType command;
}

