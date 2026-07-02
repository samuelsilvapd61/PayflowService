package com.samuel.payment.service.impl;

import com.samuel.payment.domain.request.PaymentRequest;
import com.samuel.payment.jooq.tables.records.CustomersRecord;
import com.samuel.payment.jooq.tables.records.PaymentsRecord;
import com.samuel.payment.repository.PaymentRepository;
import com.samuel.payment.service.CustomerService;
import com.samuel.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static com.samuel.payment.utils.enums.PaymentStatus.FAILED;
import static com.samuel.payment.utils.enums.PaymentStatus.SUCCESS;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final CustomerService customerService;
    private final PaymentRepository paymentRepository;

    @Override
    public void executePayment(PaymentRequest request) {

        try {
            var senderCustomer = customerService.findById(request.senderId());
            var receiverCustomer = customerService.findById(request.receiverId());

            verifyIfTheTransactionCanBeDone(senderCustomer, request);

            executeTransaction(senderCustomer, receiverCustomer, request);

        } catch (Exception e) {
            throw new RuntimeException("The transaction failed.", e);
        }
    }

    @Override
    public List<PaymentsRecord> fetchLastPayments(UUID id, LocalDate startDate) {
        return paymentRepository.fetchPayments(id, startDate);
    }

    private void executeTransaction(
            CustomersRecord senderCustomer, CustomersRecord receiverCustomer, PaymentRequest request) {

        try {
            customerService.addUserBalance(receiverCustomer.getId(), request.amount());
            customerService.addUserBalance(senderCustomer.getId(), request.amount().negate());

            paymentRepository.saveTransactionRecord(
                    senderCustomer.getId(),
                    receiverCustomer.getId(),
                    request.amount(),
                    request.description(),
                    SUCCESS
            );
        } catch (Exception e) {
            paymentRepository.saveTransactionRecord(
                    senderCustomer.getId(),
                    receiverCustomer.getId(),
                    request.amount(),
                    request.description(),
                    FAILED
            );
            throw new RuntimeException(e);
        }

    }

    private void verifyIfTheTransactionCanBeDone(CustomersRecord senderCustomer, PaymentRequest request) {

        var senderUserCurrentBalance = senderCustomer.getBalance();
        var amountToBeTransferred = request.amount();

        var valueDifference = senderUserCurrentBalance.subtract(amountToBeTransferred).longValue();

        var senderUserDoesNotHaveEnoughValueToTransfer = valueDifference < 0;

        if (senderUserDoesNotHaveEnoughValueToTransfer) {
            throw new RuntimeException("Sender User does not have enough balance to make this transaction.");
        }

    }


}
