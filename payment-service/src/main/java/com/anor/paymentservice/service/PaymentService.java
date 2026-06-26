package com.anor.paymentservice.service;

import com.anor.paymentservice.model.Card;
import com.anor.paymentservice.model.Payment;
import com.anor.paymentservice.repository.CardRepository;
import com.anor.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final CardRepository cardRepository;

    @Transactional
    public Payment processPayment(String idempotencyKey, UUID cardId, BigDecimal amount) {
        // 1. Idempotency Check: If we already processed this exact request, return the existing result.
        Optional<Payment> existingPayment = paymentRepository.findByIdempotencyKey(idempotencyKey);
        
        if (existingPayment.isPresent()) {
            Payment payment = existingPayment.get();
            // Handling the edge case: what if amount is different but key is the same?
            if (payment.getAmount().compareTo(amount) != 0) {
                throw new IllegalArgumentException("Idempotency key collision with different amount");
            }
            return payment;
        }

        // 2. Fetch the Card
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("Card not found"));

        // 3. Initialize Payment Record
        Payment payment = Payment.builder()
                .idempotencyKey(idempotencyKey)
                .cardId(cardId)
                .amount(amount)
                .createdAt(OffsetDateTime.now())
                .build();

        // 4. Atomic Balance Check & Deduction
        if (card.getBalance().compareTo(amount) >= 0) {
            card.setBalance(card.getBalance().subtract(amount));
            cardRepository.save(card);
            payment.setStatus(Payment.PaymentStatus.SUCCESS);
        } else {
            payment.setStatus(Payment.PaymentStatus.FAILED);
        }

        // 5. Save and return transaction
        return paymentRepository.save(payment);
    }
}