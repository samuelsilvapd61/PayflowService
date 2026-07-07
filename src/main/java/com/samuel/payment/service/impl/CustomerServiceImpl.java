package com.samuel.payment.service.impl;

import com.samuel.payment.domain.request.CustomerRequest;
import com.samuel.payment.domain.response.CustomerResponse;
import com.samuel.payment.repository.CustomerRepository;
import com.samuel.payment.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

import static com.samuel.payment.domain.mapper.CustomerMapper.toCustomerResponse;
import static com.samuel.payment.utils.enums.Constants.RedisCacheNames.CUSTOMERS_CACHE;
import static com.samuel.payment.utils.enums.Constants.RedisCacheNames.CUSTOMERS_KEY;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    @Cacheable(value = CUSTOMERS_CACHE, key = CUSTOMERS_KEY)
    public CustomerResponse findById(UUID id) {

        var customer = customerRepository.findById(id);

        if (customer == null) {
            throw new RuntimeException("Customer not found");
        }

        return toCustomerResponse(customer);

    }

    @Override
    public CustomerResponse createCustomer(CustomerRequest request) {

        var newCustomer = customerRepository.createCustomer(request.name(), request.email());

        if (newCustomer == null) {
            throw new RuntimeException("Customer not found");
        }

        return toCustomerResponse(newCustomer);
    }

    @Override
    @CachePut(value = CUSTOMERS_CACHE, key = CUSTOMERS_KEY)
    public CustomerResponse addUserBalance(UUID id, BigDecimal amount) {
        var customer = findById(id);
        var newBalance = customer.balance().add(amount);

        var updatedCustomer = customerRepository.updateCustomerBalance(customer.id(), newBalance);

        return toCustomerResponse(updatedCustomer);
    }

}
