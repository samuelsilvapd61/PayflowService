package com.samuel.payment.domain;

import com.samuel.payment.utils.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    private UUID id;
    private UUID senderId;
    private UUID receiverId;
    private BigDecimal amount;
    private PaymentStatus status;
    private String description;
    private LocalDateTime createdAt;

}