// src/main/java/com/anor/rental/service/RentalService.java
package com.anor.rental.service;

import com.anor.rental.domain.entity.*;
import com.anor.rental.domain.repository.*;
import com.anor.rental.exception.IdempotencyConflictException;
import com.anor.rental.exception.RentalNotFoundException;
import com.anor.rental.kafka.event.*;
import com.anor.rental.kafka.producer.RentalEventProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RentalService {

    private final RentalRepository                  rentalRepository;
    private final IdempotencyKeyRepository          idempotencyKeyRepository;
    private final RecurringPaymentChargeRepository  chargeRepository;
    private final RentalEventProducer               producer;
    private final ObjectMapper                      objectMapper;

    // ─────────────────────────────────────────────────────────────
    // CREATE RENTAL
    // ─────────────────────────────────────────────────────────────

    @Transactional
    public Rental createRental(UUID userId, UUID stationId, UUID slotId,
                               UUID cardId, String idempotencyKey) {

        // 1. Check idempotency — return the cached response if key seen before
        Optional<IdempotencyKey> existing = idempotencyKeyRepository.findByKey(idempotencyKey);
        if (existing.isPresent()) {
            try {
                return objectMapper.readValue(existing.get().getResponseBody(), Rental.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to deserialise cached rental", e);
            }
        }

        // 2. Build and persist the rental in PENDING_STATION state
        Rental rental = Rental.builder()
                .userId(userId)
                .stationId(stationId)
                .slotId(slotId)
                .cardId(cardId)
                .status(RentalStatus.PENDING_STATION)
                .ratePerHour(new BigDecimal("50.00"))
                .build();

        rental = rentalRepository.save(rental);

        // 3. Store idempotency record BEFORE sending to Kafka.
        //    If Kafka is down, the rental row exists and the key is stored;
        //    on retry the client gets the same rental back. A recovery job
        //    can later re-send the command to the station.
        saveIdempotencyKey(idempotencyKey, rental, 201);

        // 4. Ask the station to eject the powerbank
        producer.sendRentalCommand(RentalCommandEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .rentalId(rental.getId().toString())
                .stationId(stationId.toString())
                .slotId(slotId.toString())
                .command(RentalCommandEvent.CommandType.EJECT_POWERBANK)
                .build());

        log.info("Rental {} created, EJECT_POWERBANK sent to station {}", rental.getId(), stationId);
        return rental;
    }

    // ─────────────────────────────────────────────────────────────
    // FINISH RENTAL
    // ─────────────────────────────────────────────────────────────

    @Transactional
    public Rental finishRental(UUID rentalId, UUID returnStationId, UUID returnSlotId) {
        Rental rental = rentalRepository.findByIdForUpdate(rentalId)
                .orElseThrow(() -> new RentalNotFoundException(rentalId));

        if (rental.getStatus() != RentalStatus.ACTIVE) {
            throw new IllegalStateException(
                    "Cannot finish rental in status: " + rental.getStatus());
        }

        rental.setStatus(RentalStatus.PENDING_PAYMENT);
        rental.setReturnStationId(returnStationId);
        rental.setReturnSlotId(returnSlotId);
        rental.setFinishedAt(OffsetDateTime.now());

        BigDecimal totalCost = calculateCost(rental);
        rental.setTotalCost(totalCost);
        rental = rentalRepository.save(rental);

        // Tell the station to accept the powerbank return
        producer.sendRentalCommand(RentalCommandEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .rentalId(rentalId.toString())
                .stationId(returnStationId.toString())
                .slotId(returnSlotId.toString())
                .command(RentalCommandEvent.CommandType.ACCEPT_RETURN)
                .build());

        // Request final payment
        requestPayment(rental, totalCost, PaymentRequestEvent.PaymentType.FINAL_CHARGE);

        log.info("Rental {} finishing, final charge {} requested", rentalId, totalCost);
        return rental;
    }

    // ─────────────────────────────────────────────────────────────
    // QUERIES
    // ─────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Rental getRentalStatus(UUID rentalId) {
        return rentalRepository.findById(rentalId)
                .orElseThrow(() -> new RentalNotFoundException(rentalId));
    }

    @Transactional(readOnly = true)
    public Page<Rental> getRentalHistory(UUID userId, int page, int size) {
        return rentalRepository.findByUserIdOrderByCreatedAtDesc(
                userId, PageRequest.of(page, size));
    }

    // ─────────────────────────────────────────────────────────────
    // STATION CALLBACKS (called from Kafka consumer)
    // ─────────────────────────────────────────────────────────────

    @Transactional
    public void onPowerbankEjected(StationResponseEvent event) {
        Rental rental = getForUpdate(UUID.fromString(event.getRentalId()));
        guardStatus(rental, RentalStatus.PENDING_STATION);

        rental.setStatus(RentalStatus.ACTIVE);
        rental.setPowerbankId(UUID.fromString(event.getPowerbankId()));
        rental.setStartedAt(OffsetDateTime.now());
        rental.setLastBilledAt(OffsetDateTime.now());
        rentalRepository.save(rental);

        log.info("Rental {} is now ACTIVE, powerbank {}", rental.getId(), rental.getPowerbankId());
    }

    @Transactional
    public void onPowerbankReturned(StationResponseEvent event) {
        Rental rental = getForUpdate(UUID.fromString(event.getRentalId()));
        rental.setStatus(RentalStatus.COMPLETED);
        rentalRepository.save(rental);
        log.info("Rental {} COMPLETED", rental.getId());
    }

    @Transactional
    public void onEjectionFailed(StationResponseEvent event) {
        Rental rental = getForUpdate(UUID.fromString(event.getRentalId()));
        rental.setStatus(RentalStatus.FAILED);
        rentalRepository.save(rental);
        log.warn("Rental {} FAILED — ejection error: {}", rental.getId(), event.getErrorMessage());
    }

    // ─────────────────────────────────────────────────────────────
    // PAYMENT CALLBACKS
    // ─────────────────────────────────────────────────────────────

    @Transactional
    public void onPaymentResult(PaymentResultEvent event) {
        if (event.getChargeId() != null) {
            // Update the specific recurring charge record
            chargeRepository.findById(UUID.fromString(event.getChargeId()))
                    .ifPresent(charge -> {
                        charge.setStatus(
                                event.getStatus() == PaymentResultEvent.PaymentStatus.SUCCESS
                                        ? RecurringPaymentCharge.PaymentChargeStatus.SUCCESS
                                        : RecurringPaymentCharge.PaymentChargeStatus.FAILED);
                        chargeRepository.save(charge);
                    });
        }

        if (event.getStatus() == PaymentResultEvent.PaymentStatus.FAILED) {
            log.warn("Payment failed for rental {} — reason: {}",
                    event.getRentalId(), event.getFailureReason());
            // Policy choice: keep rental open; alert user; retry handled by scheduler.
        }
    }

    // ─────────────────────────────────────────────────────────────
    // RECURRING PAYMENTS (called by scheduler)
    // ─────────────────────────────────────────────────────────────

    @Transactional
    public void processRecurringPayments() {
        List<Rental> activeRentals = rentalRepository.findByStatus(RentalStatus.ACTIVE);

        for (Rental rental : activeRentals) {
            OffsetDateTime now = OffsetDateTime.now();
            OffsetDateTime lastBilled = rental.getLastBilledAt();

            if (lastBilled == null ||
                    Duration.between(lastBilled, now).toMinutes() >= 60) {

                BigDecimal charge = rental.getRatePerHour();

                RecurringPaymentCharge record = chargeRepository.save(
                        RecurringPaymentCharge.builder()
                                .rentalId(rental.getId())
                                .amount(charge)
                                .status(RecurringPaymentCharge.PaymentChargeStatus.PENDING)
                                .chargedAt(now)
                                .build()
                );

                requestPaymentWithChargeId(rental, charge,
                        PaymentRequestEvent.PaymentType.RECURRING_CHARGE,
                        record.getId().toString());

                rental.setLastBilledAt(now);
                rentalRepository.save(rental);

                log.info("Recurring charge {} queued for rental {}", charge, rental.getId());
            }
        }
    }

    // ─────────────────────────────────────────────────────────────
    // PRIVATE HELPERS
    // ─────────────────────────────────────────────────────────────

    private BigDecimal calculateCost(Rental rental) {
        if (rental.getStartedAt() == null) return BigDecimal.ZERO;
        long minutes = Duration.between(rental.getStartedAt(), rental.getFinishedAt()).toMinutes();
        // pro-rata billing per minute
        return rental.getRatePerHour()
                .multiply(BigDecimal.valueOf(minutes))
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
    }

    private void requestPayment(Rental rental, BigDecimal amount,
                                PaymentRequestEvent.PaymentType type) {
        requestPaymentWithChargeId(rental, amount, type, null);
    }

    private void requestPaymentWithChargeId(Rental rental, BigDecimal amount,
                                            PaymentRequestEvent.PaymentType type,
                                            String chargeId) {
        // Use chargeId (or a fresh UUID) as the idempotency key for payment-service
        String eventId = chargeId != null ? chargeId : UUID.randomUUID().toString();

        producer.sendPaymentRequest(PaymentRequestEvent.builder()
                .eventId(eventId)
                .rentalId(rental.getId().toString())
                .userId(rental.getUserId().toString())
                .cardId(rental.getCardId().toString())
                .amount(amount)
                .paymentType(type)
                .build());
    }

    private Rental getForUpdate(UUID rentalId) {
        return rentalRepository.findByIdForUpdate(rentalId)
                .orElseThrow(() -> new RentalNotFoundException(rentalId));
    }

    private void guardStatus(Rental rental, RentalStatus expected) {
        if (rental.getStatus() != expected) {
            throw new IllegalStateException(
                    "Expected status " + expected + " but was " + rental.getStatus());
        }
    }

    private void saveIdempotencyKey(String key, Rental rental, int statusCode) {
        try {
            String body = objectMapper.writeValueAsString(rental);
            idempotencyKeyRepository.save(IdempotencyKey.builder()
                    .key(key)
                    .responseBody(body)
                    .statusCode(statusCode)
                    .createdAt(OffsetDateTime.now())
                    .build());
        } catch (JsonProcessingException e) {
            log.error("Failed to serialise idempotency response for key {}", key, e);
        }
    }
}
