package com.samuel.payment.domain.mapper;

import com.samuel.payment.domain.response.PaymentResponse;
import com.samuel.payment.jooq.tables.records.PaymentsRecord;

import java.util.List;

import static com.samuel.payment.utils.enums.PaymentStatus.valueOf;

public class PaymentMapper {

    public static PaymentResponse toPaymentResponse(PaymentsRecord payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getSenderId(),
                payment.getReceiverId(),
                payment.getAmount(),
                valueOf(payment.getStatus()),
                payment.getDescription(),
                payment.getCreatedAt()
        );
    }

    public static List<PaymentResponse> toPaymentResponseList(List<PaymentsRecord> paymentRecordList) {
        return paymentRecordList.stream().map(PaymentMapper::toPaymentResponse).toList();
    }

}
