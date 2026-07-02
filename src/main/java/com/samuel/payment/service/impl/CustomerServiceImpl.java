package com.samuel.payment.service.impl;

import com.samuel.payment.domain.request.CustomerRequest;
import com.samuel.payment.jooq.tables.records.CustomersRecord;
import com.samuel.payment.repository.CustomerRepository;
import com.samuel.payment.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    public CustomersRecord findById(UUID id) {

        var customer = customerRepository.findById(id);

        if (customer == null) {
            throw new RuntimeException("Customer not found");
        }
        return customer;

    }

    @Override
    public CustomersRecord createCustomer(CustomerRequest request) {

        var newCustomer = customerRepository.createCustomer(request.name(), request.email());

        if (newCustomer == null) {
            throw new RuntimeException("Customer not found");
        }
        return newCustomer;
    }

    @Override
    public CustomersRecord addUserBalance(UUID id, BigDecimal amount) {
        var customer = findById(id);
        var newBalance = customer.getBalance().add(amount);

        return customerRepository.updateCustomerBalance(customer.getId(), newBalance);
    }

}
