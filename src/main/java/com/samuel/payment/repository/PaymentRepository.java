package com.samuel.payment.repository;

import com.samuel.payment.generated.jooq.tables.records.PaymentsRecord;
import com.samuel.payment.utils.enums.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static com.samuel.payment.generated.jooq.Tables.PAYMENTS;
import static java.time.LocalDateTime.now;


@Repository
@RequiredArgsConstructor
public class PaymentRepository {

    private final DSLContext dsl;

    public void saveTransactionRecord(
            UUID senderId, UUID receiverId, BigDecimal amount, String description, PaymentStatus status) {

        var paymentsRecord = dsl.newRecord(PAYMENTS);

        paymentsRecord.setSenderId(senderId);
        paymentsRecord.setReceiverId(receiverId);
        paymentsRecord.setAmount(amount);
        paymentsRecord.setDescription(description);
        paymentsRecord.setStatus(status.toString());
        paymentsRecord.setCreatedAt(now());

        paymentsRecord.store();
    }

    public List<PaymentsRecord> fetchPayments(UUID id, LocalDate startDate) {
        var date = startDate.atStartOfDay();

        return dsl
                .selectFrom(PAYMENTS)
                .where(
                        PAYMENTS.SENDER_ID.eq(id).or(PAYMENTS.RECEIVER_ID.eq(id))
                )
                .and(PAYMENTS.CREATED_AT.ge(date))
                .stream().toList();
    }
}
