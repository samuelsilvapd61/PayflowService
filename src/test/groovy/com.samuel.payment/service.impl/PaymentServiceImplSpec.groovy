package com.samuel.payment.service.impl

import com.samuel.payment.domain.request.PaymentRequest
import com.samuel.payment.domain.response.CustomerResponse
import com.samuel.payment.generated.jooq.tables.records.PaymentsRecord
import com.samuel.payment.repository.PaymentRepository
import com.samuel.payment.service.CustomerService
import com.samuel.payment.service.PaymentService
import spock.lang.Specification

import java.time.LocalDate
import java.time.LocalDateTime

import static com.samuel.payment.utils.enums.PaymentStatus.FAILED
import static com.samuel.payment.utils.enums.PaymentStatus.SUCCESS

class PaymentServiceImplSpec extends Specification {

    CustomerService customerService = Mock()
    PaymentRepository paymentRepository = Mock()
    PaymentService paymentService

    void setup() {
        paymentService = new PaymentServiceImpl(customerService, paymentRepository)
    }

    def "should execute transaction"() {
        given:
        def senderCustomer = buildCustomerResponse()
        def receiverCustomer = buildCustomerResponse()
        def amount = BigDecimal.valueOf(5)
        def description = "Description Test"
        def request = new PaymentRequest(
                senderCustomer.id(), receiverCustomer.id(), amount, description)

        customerService.findById(senderCustomer.id()) >> senderCustomer
        customerService.findById(receiverCustomer.id()) >> receiverCustomer

        customerService.addUserBalance(receiverCustomer.id(), request.amount()) >> _
        customerService.addUserBalance(senderCustomer.id(), request.amount().negate()) >> _


        when:
        paymentService.executePayment(request)


        then:
        1 * customerService.addUserBalance(receiverCustomer.id(), request.amount()) >> _
        1 * customerService.addUserBalance(senderCustomer.id(), request.amount().negate()) >> _
        1 * paymentRepository.saveTransactionRecord(
                senderCustomer.id(), receiverCustomer.id(), amount, description, SUCCESS)

    }

    def "should throw exception because senderCustomer doesn't have enough balance"() {
        given:
        def senderCustomer = buildCustomerResponse()
        def receiverCustomer = buildCustomerResponse()
        def amount = BigDecimal.valueOf(15)
        def description = "Description Test"
        def request = new PaymentRequest(
                senderCustomer.id(), receiverCustomer.id(), amount, description)

        customerService.findById(senderCustomer.id()) >> senderCustomer
        customerService.findById(receiverCustomer.id()) >> receiverCustomer

        when:
        paymentService.executePayment(request)

        then:
        thrown(RuntimeException)
        0 * customerService.addUserBalance(receiverCustomer.id(), request.amount()) >> _
        0 * customerService.addUserBalance(senderCustomer.id(), request.amount().negate()) >> _
        0 * paymentRepository.saveTransactionRecord(
                senderCustomer.id(), receiverCustomer.id(), amount, description, SUCCESS)
        1 * paymentRepository.saveTransactionRecord(
                senderCustomer.id(), receiverCustomer.id(), amount, description, FAILED)

    }

    def "should return some payment records"() {
        given:
        def id = UUID.randomUUID()
        def date = LocalDate.now()

        def paymentsList = List.of(
                new PaymentsRecord(UUID.randomUUID(), id, UUID.randomUUID(), BigDecimal.TEN, SUCCESS.toString(), "Description Test", LocalDateTime.now()),
                new PaymentsRecord(UUID.randomUUID(), id, UUID.randomUUID(), BigDecimal.TEN, FAILED.toString(), "Description Test", LocalDateTime.now())
        )

        paymentRepository.fetchPayments(id, date) >> paymentsList


        when:
        def response = paymentService.fetchLastPayments(id, date)

        then:
        response == paymentsList
    }

    private static CustomerResponse buildCustomerResponse() {
        return new CustomerResponse(
                UUID.randomUUID(),
                "Name Test",
                "email test",
                BigDecimal.TEN,
                LocalDateTime.now(),
                LocalDateTime.now()
        )
    }

}
