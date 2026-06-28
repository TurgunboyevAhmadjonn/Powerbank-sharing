// event/StationEvent.java  (published to station-events)
package com.anor.station.event;

import java.time.OffsetDateTime;

public record StationEvent(
        String eventId,
        String type,          // POWER_BANK_DISPENSED / DISPENSE_FAILED / POWER_BANK_RETURNED
        String rentalId,
        String stationId,
        String powerBankId,
        Integer slotNumber,
        String reason,        // populated on failure
        OffsetDateTime occurredAt
) {}


