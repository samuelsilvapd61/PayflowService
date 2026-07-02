package com.samuel.payment.domain.mapper;

import com.samuel.payment.domain.response.CustomerResponse;
import com.samuel.payment.jooq.tables.records.CustomersRecord;

public class CustomerMapper {

    public static CustomerResponse toCustomerResponse(CustomersRecord customer) {

        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getBalance(),
                customer.getCreatedAt(),
                customer.getUpdatedAt()
        );

    }

}
