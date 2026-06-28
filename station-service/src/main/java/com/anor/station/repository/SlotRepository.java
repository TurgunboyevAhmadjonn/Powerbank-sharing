package com.anor.station.repository;

import com.anor.station.domain.*;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface SlotRepository extends JpaRepository<Slot, UUID> {

    List<Slot> findByStationId(UUID stationId);

    // Pessimistic lock: pick one charged occupied slot and hold the row
    // so two concurrent dispense commands can't grab the same power bank.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT s FROM Slot s
        JOIN FETCH s.powerBank pb
        WHERE s.station.id = :stationId
          AND s.status = com.anor.station.domain.SlotStatus.OCCUPIED
          AND pb.status = com.anor.station.domain.PowerBankStatus.IN_SLOT
          AND pb.chargeLevel >= :minCharge
        ORDER BY pb.chargeLevel DESC
        """)
    List<Slot> lockAvailableSlots(@Param("stationId") UUID stationId,
                                  @Param("minCharge") int minCharge);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT s FROM Slot s
        WHERE s.station.id = :stationId
          AND s.status = com.anor.station.domain.SlotStatus.EMPTY
        ORDER BY s.slotNumber ASC
        """)
    List<Slot> lockEmptySlots(@Param("stationId") UUID stationId);
}

