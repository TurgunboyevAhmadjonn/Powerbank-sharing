package com.anor.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResultEvent {
    private UUID paymentId;
    private String idempotencyKey;
    private String status;
}