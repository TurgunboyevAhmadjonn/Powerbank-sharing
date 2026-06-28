// src/test/java/com/anor/rental/service/RentalServiceTest.java
package com.anor.rental.service;

import com.anor.rental.domain.entity.Rental;
import com.anor.rental.domain.entity.RentalStatus;
import com.anor.rental.domain.repository.IdempotencyKeyRepository;
import com.anor.rental.domain.repository.RecurringPaymentChargeRepository;
import com.anor.rental.domain.repository.RentalRepository;
import com.anor.rental.kafka.event.StationResponseEvent;
import com.anor.rental.kafka.producer.RentalEventProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RentalServiceTest {

    @Mock RentalRepository              rentalRepository;
    @Mock IdempotencyKeyRepository      idempotencyKeyRepository;
    @Mock RecurringPaymentChargeRepository chargeRepository;
    @Mock RentalEventProducer           producer;
    @Mock ObjectMapper                  objectMapper;

    @InjectMocks
    RentalService rentalService;

    private UUID userId, stationId, slotId, cardId, rentalId;

    @BeforeEach
    void setUp() {
        userId    = UUID.randomUUID();
        stationId = UUID.randomUUID();
        slotId    = UUID.randomUUID();
        cardId    = UUID.randomUUID();
        rentalId  = UUID.randomUUID();
    }

    @Test
    @DisplayName("createRental — happy path persists PENDING_STATION rental")
    void createRental_happyPath() {
        when(idempotencyKeyRepository.findByKey(any())).thenReturn(Optional.empty());

        Rental saved = Rental.builder()
                .id(rentalId).userId(userId).stationId(stationId)
                .slotId(slotId).cardId(cardId)
                .status(RentalStatus.PENDING_STATION)
                .ratePerHour(new BigDecimal("50.00"))
                .build();
        when(rentalRepository.save(any())).thenReturn(saved);

        Rental result = rentalService.createRental(userId, stationId, slotId,
                cardId, "idem-key-1");

        assertThat(result.getStatus()).isEqualTo(RentalStatus.PENDING_STATION);
        verify(producer).sendRentalCommand(any());
    }

    @Test
    @DisplayName("createRental — duplicate idempotency key returns cached rental")
    void createRental_idempotentReturn() throws Exception {
        Rental cachedRental = Rental.builder().id(rentalId)
                .status(RentalStatus.PENDING_STATION).build();
        String json = new ObjectMapper().writeValueAsString(cachedRental);

        when(idempotencyKeyRepository.findByKey("dup-key"))
                .thenReturn(Optional.of(
                        com.anor.rental.domain.entity.IdempotencyKey.builder()
                                .key("dup-key").responseBody(json).statusCode(201)
                                .createdAt(OffsetDateTime.now()).build()));
        // Use real ObjectMapper for deserialisation
        when(objectMapper.readValue(eq(json), eq(Rental.class))).thenReturn(cachedRental);

        Rental result = rentalService.createRental(userId, stationId, slotId, cardId, "dup-key");

        assertThat(result.getId()).isEqualTo(rentalId);
        // No new rental should be saved
        verify(rentalRepository, never()).save(any());
        verify(producer, never()).sendRentalCommand(any());
    }

    @Test
    @DisplayName("onPowerbankEjected — transitions PENDING_STATION → ACTIVE")
    void onPowerbankEjected_setsActive() {
        UUID powerbankId = UUID.randomUUID();
        Rental pending = Rental.builder()
                .id(rentalId).status(RentalStatus.PENDING_STATION)
                .ratePerHour(new BigDecimal("50.00"))
                .build();

        when(rentalRepository.findByIdForUpdate(rentalId)).thenReturn(Optional.of(pending));
        when(rentalRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        rentalService.onPowerbankEjected(StationResponseEvent.builder()
                .rentalId(rentalId.toString())
                .powerbankId(powerbankId.toString())
                .eventType(StationResponseEvent.EventType.POWERBANK_EJECTED)
                .build());

        assertThat(pending.getStatus()).isEqualTo(RentalStatus.ACTIVE);
        assertThat(pending.getPowerbankId()).isEqualTo(powerbankId);
        assertThat(pending.getStartedAt()).isNotNull();
    }

    @Test
    @DisplayName("finishRental — throws when rental is not ACTIVE")
    void finishRental_notActive_throws() {
        Rental completed = Rental.builder()
                .id(rentalId).status(RentalStatus.COMPLETED).build();
        when(rentalRepository.findByIdForUpdate(rentalId)).thenReturn(Optional.of(completed));

        assertThatThrownBy(() ->
                rentalService.finishRental(rentalId, UUID.randomUUID(), UUID.randomUUID()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("COMPLETED");
    }
}
