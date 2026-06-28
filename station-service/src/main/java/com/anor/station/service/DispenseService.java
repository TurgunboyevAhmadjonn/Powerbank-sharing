// service/DispenseService.java
package com.anor.station.service;

import com.anor.station.domain.*;
import com.anor.station.event.*;
import com.anor.station.kafka.StationEventPublisher;
import com.anor.station.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class DispenseService {

    private static final int MIN_CHARGE = 20;

    private final SlotRepository slotRepository;
    private final PowerBankRepository powerBankRepository;
    private final StationEventPublisher publisher;

    /**
     * Emulates an IoT station. The "physical" ejection is asynchronous, so we
     * do the DB state transition transactionally, then emit the result event.
     */
    @Transactional
    public void handleDispense(DispenseCommand cmd) {
        UUID stationId = UUID.fromString(cmd.stationId());

        List<Slot> candidates = slotRepository.lockAvailableSlots(stationId, MIN_CHARGE);
        if (candidates.isEmpty()) {
            publisher.publish(failure(cmd, "NO_AVAILABLE_POWER_BANK"));
            return;
        }

        Slot slot = candidates.get(0);
        PowerBank pb = slot.getPowerBank();

        // transition
        pb.setStatus(PowerBankStatus.IN_USE);
        slot.setStatus(SlotStatus.EMPTY);
        slot.setPowerBank(null);

        powerBankRepository.save(pb);
        slotRepository.save(slot);

        publisher.publish(new StationEvent(
                UUID.randomUUID().toString(),
                "POWER_BANK_DISPENSED",
                cmd.rentalId(),
                cmd.stationId(),
                pb.getId().toString(),
                slot.getSlotNumber(),
                null,
                OffsetDateTime.now()
        ));
    }

    @Transactional
    public void handleReturn(ReturnCommand cmd) {
        UUID stationId = UUID.fromString(cmd.stationId());

        PowerBank pb = powerBankRepository.findBySerial(cmd.powerBankSerial())
                .orElse(null);
        if (pb == null) {
            publisher.publish(returnFailure(cmd, "UNKNOWN_POWER_BANK"));
            return;
        }

        List<Slot> empty = slotRepository.lockEmptySlots(stationId);
        if (empty.isEmpty()) {
            publisher.publish(returnFailure(cmd, "STATION_FULL"));
            return;
        }

        Slot slot = empty.get(0);
        pb.setStatus(PowerBankStatus.IN_SLOT);
        slot.setStatus(SlotStatus.OCCUPIED);
        slot.setPowerBank(pb);

        powerBankRepository.save(pb);
        slotRepository.save(slot);

        publisher.publish(new StationEvent(
                UUID.randomUUID().toString(),
                "POWER_BANK_RETURNED",
                cmd.rentalId(),
                cmd.stationId(),
                pb.getId().toString(),
                slot.getSlotNumber(),
                null,
                OffsetDateTime.now()
        ));
    }

    private StationEvent failure(DispenseCommand cmd, String reason) {
        return new StationEvent(UUID.randomUUID().toString(), "DISPENSE_FAILED",
                cmd.rentalId(), cmd.stationId(), null, null, reason, OffsetDateTime.now());
    }

    private StationEvent returnFailure(ReturnCommand cmd, String reason) {
        return new StationEvent(UUID.randomUUID().toString(), "RETURN_FAILED",
                cmd.rentalId(), cmd.stationId(), null, null, reason, OffsetDateTime.now());
    }
}

