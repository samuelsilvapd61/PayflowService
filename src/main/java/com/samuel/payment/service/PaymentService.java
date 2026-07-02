package com.samuel.payment.service;

import com.samuel.payment.domain.request.PaymentRequest;
import com.samuel.payment.jooq.tables.records.PaymentsRecord;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface PaymentService {

    void executePayment(PaymentRequest request);

    List<PaymentsRecord> fetchLastPayments(UUID id, LocalDate startDate);

}
