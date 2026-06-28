
package com.anor.station.event;

public record DispenseCommand(
        String idempotencyKey,
        String rentalId,
        String stationId,
        String userId
) {}

