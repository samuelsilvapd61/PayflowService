package com.samuel.payment.service;

import com.samuel.payment.domain.request.CustomerRequest;
import com.samuel.payment.domain.response.CustomerResponse;

import java.math.BigDecimal;
import java.util.UUID;

public interface CustomerService {

    CustomerResponse findById(UUID id);

    CustomerResponse createCustomer(CustomerRequest request);

    CustomerResponse addUserBalance(UUID id, BigDecimal amount);

}
