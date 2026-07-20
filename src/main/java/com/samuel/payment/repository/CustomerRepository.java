package com.samuel.payment.repository;

import com.samuel.payment.generated.jooq.tables.records.CustomersRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.UUID;

import static com.samuel.payment.generated.jooq.Tables.CUSTOMERS;
import static java.time.LocalDateTime.now;


@Repository
@RequiredArgsConstructor
public class CustomerRepository {

    private final DSLContext dsl;

    public CustomersRecord findById(UUID id) {
        return dsl
                .selectFrom(CUSTOMERS)
                .where(CUSTOMERS.ID.eq(id))
                .fetchOne();
    }

    public CustomersRecord createCustomer(String name, String email) {
        var customerRecord = dsl.newRecord(CUSTOMERS);

        var timeNow = now();
        customerRecord.setName(name);
        customerRecord.setEmail(email);
        customerRecord.setBalance(BigDecimal.ZERO);
        customerRecord.setCreatedAt(timeNow);
        customerRecord.setUpdatedAt(timeNow);

        customerRecord.store();

        return customerRecord;
    }

    public CustomersRecord updateCustomerBalance(UUID id, BigDecimal newBalance) {
        return dsl
                .update(CUSTOMERS)
                .set(CUSTOMERS.BALANCE, newBalance)
                .set(CUSTOMERS.UPDATED_AT, now())
                .where(CUSTOMERS.ID.eq(id))
                .returning()
                .fetchOne();
    }
}
