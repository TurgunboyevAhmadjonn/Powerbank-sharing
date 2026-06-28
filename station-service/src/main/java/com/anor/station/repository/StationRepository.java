package com.anor.station.repository;

import com.anor.station.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StationRepository extends JpaRepository<Station, UUID> {

    interface StationNearbyView {
        UUID getId();
        String getName();
        double getLat();
        double getLng();
        String getStatus();
        int getTotalSlots();
        double getDistanceMeters();
    }

    @Query(value = """
        SELECT s.*,
               (6371000 * acos(
                   cos(radians(:lat)) * cos(radians(s.lat)) *
                   cos(radians(s.lng) - radians(:lng)) +
                   sin(radians(:lat)) * sin(radians(s.lat))
               )) AS distance
        FROM station s
        WHERE s.status = 'ONLINE'
          AND (6371000 * acos(
                   cos(radians(:lat)) * cos(radians(s.lat)) *
                   cos(radians(s.lng) - radians(:lng)) +
                   sin(radians(:lat)) * sin(radians(s.lat))
               )) <= :radiusMeters
        ORDER BY distance ASC
        LIMIT :limit
        """, nativeQuery = true)
    List<Station> findNearestStations(
            @Param("lat") double latitude,
            @Param("lng") double longitude,
            @Param("radiusMeters") int radiusMeters,
            @Param("limit") int limit
    );

    @Query(value = """
        SELECT s.id AS id,
               s.name AS name,
               s.lat AS lat,
               s.lng AS lng,
               s.status AS status,
               s.total_slots AS totalSlots,
               (6371000 * acos(
                   cos(radians(:lat)) * cos(radians(s.lat)) *
                   cos(radians(s.lng) - radians(:lng)) +
                   sin(radians(:lat)) * sin(radians(s.lat))
               )) AS distanceMeters
        FROM station s
        WHERE s.status = 'ONLINE'
          AND (6371000 * acos(
                   cos(radians(:lat)) * cos(radians(s.lat)) *
                   cos(radians(s.lng) - radians(:lng)) +
                   sin(radians(:lat)) * sin(radians(s.lat))
               )) <= :radiusMeters
        ORDER BY distanceMeters ASC
        LIMIT :limit
        """, nativeQuery = true)
    List<StationNearbyView> findNearestStationViews(
            @Param("lat") double latitude,
            @Param("lng") double longitude,
            @Param("radiusMeters") int radiusMeters,
            @Param("limit") int limit
    );

    List<Station> findByStatus(String status);
}
