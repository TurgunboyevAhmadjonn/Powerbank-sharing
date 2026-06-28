// src/main/java/com/anor/rental/domain/repository/RecurringPaymentChargeRepository.java
package com.anor.rental.domain.repository;

import com.anor.rental.domain.entity.RecurringPaymentCharge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RecurringPaymentChargeRepository extends JpaRepository<RecurringPaymentCharge, UUID> {

    List<RecurringPaymentCharge> findByRentalId(UUID rentalId);
}

