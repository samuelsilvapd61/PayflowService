package com.samuel.payment.domain.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CustomerResponse(
        UUID id,
        String name,
        String email,
        BigDecimal balance,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

}
