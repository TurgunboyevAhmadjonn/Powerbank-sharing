// event/ReturnCommand.java  (a power bank physically inserted; emulated)
package com.anor.station.event;

public record ReturnCommand(
        String idempotencyKey,
        String rentalId,
        String stationId,
        String powerBankSerial
) {}
