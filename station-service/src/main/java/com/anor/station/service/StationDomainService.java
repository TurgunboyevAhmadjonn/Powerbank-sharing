// src/main/java/com/anor/station/service/StationDomainService.java
package com.anor.station.service;

import com.anor.station.domain.Slot;
import com.anor.station.domain.Station;
import com.anor.station.repository.SlotRepository;
import com.anor.station.repository.StationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StationDomainService {

    private final StationRepository stationRepository;
    private final SlotRepository slotRepository;

    @Transactional(readOnly = true)
    public List<Station> findNearby(double lat, double lng, double radiusMeters) {
        return stationRepository.findNearestStations(lat, lng, (int) radiusMeters, 20);
    }

    @Transactional(readOnly = true)
    public Optional<Station> getStation(UUID id) {
        return stationRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Slot> slotsOf(UUID stationId) {
        return slotRepository.findByStationId(stationId);
    }
}
