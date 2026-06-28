// src/main/java/com/anor/rental/domain/repository/RentalRepository.java
package com.anor.rental.domain.repository;

import com.anor.rental.domain.entity.Rental;
import com.anor.rental.domain.entity.RentalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RentalRepository extends JpaRepository<Rental, UUID> {

    Page<Rental> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    List<Rental> findByStatus(RentalStatus status);

    Optional<Rental> findByPowerbankId(UUID powerbankId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Rental r WHERE r.id = :id")
    Optional<Rental> findByIdForUpdate(@Param("id") UUID id);
}
