package com.samuel.payment.domain.request;

import java.math.BigDecimal;

public record AddUserBalanceRequest(BigDecimal amount) {

}
