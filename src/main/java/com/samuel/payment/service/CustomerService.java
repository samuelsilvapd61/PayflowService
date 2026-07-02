package com.samuel.payment.service;

import com.samuel.payment.domain.request.CustomerRequest;
import com.samuel.payment.jooq.tables.records.CustomersRecord;

import java.math.BigDecimal;
import java.util.UUID;

public interface CustomerService {

    CustomersRecord findById(UUID id);

    CustomersRecord createCustomer(CustomerRequest request);

    CustomersRecord addUserBalance(UUID id, BigDecimal amount);

}
