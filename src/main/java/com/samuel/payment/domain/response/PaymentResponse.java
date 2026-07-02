package com.samuel.payment.domain.response;

import com.samuel.payment.utils.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentResponse(
        UUID id,
        UUID senderId,
        UUID receiverId,
        BigDecimal amount,
        PaymentStatus status,
        String description,
        LocalDateTime createdAt
) {

}
