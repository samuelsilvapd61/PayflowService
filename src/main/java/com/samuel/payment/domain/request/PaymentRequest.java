package com.samuel.payment.domain.request;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentRequest(
        UUID senderId,
        UUID receiverId,
        BigDecimal amount,
        String description
) {

}
