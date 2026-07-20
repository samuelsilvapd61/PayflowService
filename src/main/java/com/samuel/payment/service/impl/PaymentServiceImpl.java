package com.samuel.payment.service.impl;

import com.samuel.payment.domain.request.PaymentRequest;
import com.samuel.payment.domain.response.CustomerResponse;
import com.samuel.payment.generated.jooq.tables.records.PaymentsRecord;
import com.samuel.payment.repository.PaymentRepository;
import com.samuel.payment.service.CustomerService;
import com.samuel.payment.service.PaymentService;
import com.samuel.payment.utils.enums.PaymentStatus;
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

            customerService.addUserBalance(receiverCustomer.id(), request.amount());
            customerService.addUserBalance(senderCustomer.id(), request.amount().negate());

            saveTransactionRecord(request.senderId(), request.receiverId(), request, SUCCESS);

        } catch (Exception e) {
            saveTransactionRecord(request.senderId(), request.receiverId(), request, FAILED);
            throw new RuntimeException("The transaction failed.", e);
        }
    }

    @Override
    public List<PaymentsRecord> fetchLastPayments(UUID id, LocalDate startDate) {
        return paymentRepository.fetchPayments(id, startDate);
    }

    private void verifyIfTheTransactionCanBeDone(CustomerResponse senderCustomer, PaymentRequest request) {

        var senderUserCurrentBalance = senderCustomer.balance();
        var amountToBeTransferred = request.amount();

        var valueDifference = senderUserCurrentBalance.subtract(amountToBeTransferred).longValue();

        var senderUserDoesNotHaveEnoughValueToTransfer = valueDifference < 0;

        if (senderUserDoesNotHaveEnoughValueToTransfer) {
            throw new RuntimeException("Sender User does not have enough balance to make this transaction.");
        }

    }

    private void saveTransactionRecord(
            UUID senderCustomerId, UUID receiverCustomerId, PaymentRequest request, PaymentStatus paymentStatus) {

        paymentRepository.saveTransactionRecord(
                senderCustomerId,
                receiverCustomerId,
                request.amount(),
                request.description(),
                paymentStatus
        );

    }
}
