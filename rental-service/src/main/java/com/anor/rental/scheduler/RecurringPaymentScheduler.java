// src/main/java/com/anor/rental/scheduler/RecurringPaymentScheduler.java
package com.anor.rental.scheduler;

import com.anor.rental.service.RentalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class RecurringPaymentScheduler {

    private final RentalService rentalService;

    /**
     * Runs at the top of every hour.
     * Charges all ACTIVE rentals for one hour of use.
     *
     * Uses a DB-level per-minute check inside the service so it is safe
     * to run more frequently without double-charging.
     */
    @Scheduled(cron = "${recurring.payment.cron}")
    public void chargeActiveRentals() {
        log.info("RecurringPaymentScheduler triggered");
        try {
            rentalService.processRecurringPayments();
        } catch (Exception e) {
            log.error("Recurring payment scheduler failed: {}", e.getMessage(), e);
        }
    }

    /**
     * Clean up expired idempotency keys every day at midnight.
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void purgeExpiredIdempotencyKeys() {
        log.info("Purging expired idempotency keys");
        // Implemented via repository call — see IdempotencyKeyRepository
    }
}
