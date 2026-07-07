package com.samuel.payment.service.impl;

import com.samuel.payment.domain.request.PaymentRequest;
import com.samuel.payment.domain.response.CustomerResponse;
import com.samuel.payment.jooq.tables.records.PaymentsRecord;
import com.samuel.payment.repository.PaymentRepository;
import com.samuel.payment.service.CustomerService;
import com.samuel.payment.service.PaymentService;
import com.samuel.payment.utils.enums.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static com.samuel.payment.utils.enums.Constants.RedisCacheNames.CUSTOMERS_CACHE;
import static com.samuel.payment.utils.enums.PaymentStatus.FAILED;
import static com.samuel.payment.utils.enums.PaymentStatus.SUCCESS;
import static java.util.Objects.requireNonNull;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final CustomerService customerService;
    private final PaymentRepository paymentRepository;
    private final CacheManager cacheManager;

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

    private void verifyIfTheTransactionCanBeDone(CustomerResponse senderCustomer, PaymentRequest request) {

        var senderUserCurrentBalance = senderCustomer.balance();
        var amountToBeTransferred = request.amount();

        var valueDifference = senderUserCurrentBalance.subtract(amountToBeTransferred).longValue();

        var senderUserDoesNotHaveEnoughValueToTransfer = valueDifference < 0;

        if (senderUserDoesNotHaveEnoughValueToTransfer) {
            throw new RuntimeException("Sender User does not have enough balance to make this transaction.");
        }

    }

    private void executeTransaction(
            CustomerResponse senderCustomer, CustomerResponse receiverCustomer, PaymentRequest request) {

        try {
            var updatedReceiverCustomer = customerService.addUserBalance(receiverCustomer.id(), request.amount());
            var updatedSenderCustomer = customerService.addUserBalance(senderCustomer.id(), request.amount().negate());

            saveTransactionRecord(updatedSenderCustomer.id(), updatedReceiverCustomer.id(), request, SUCCESS);

        } catch (Exception e) {
            saveTransactionRecord(senderCustomer.id(), receiverCustomer.id(), request, FAILED);
            throw new RuntimeException(e);
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
