package com.samuel.payment.service.impl

import com.samuel.payment.domain.request.CustomerRequest
import com.samuel.payment.jooq.tables.records.CustomersRecord
import com.samuel.payment.repository.CustomerRepository
import com.samuel.payment.service.CustomerService
import spock.lang.Specification

import java.time.LocalDateTime

class CustomerServiceImplSpec extends Specification {

    CustomerRepository customerRepository = Stub()
    CustomerService customerService

    void setup() {
        customerService = new CustomerServiceImpl(customerRepository)
    }

    def "should find a user by id"() {
        given:
        def uuid = UUID.randomUUID()
        def customer = buildDefaultCustomer()
        customer.setId(uuid)

        customerRepository.findById(uuid) >> customer

        when:
        def foundCustomer = customerService.findById(uuid)

        then:
        foundCustomer != null
        foundCustomer.id() == uuid

    }

    def "should not find a user by id and throw an exception"() {
        given:
        def uuid = UUID.randomUUID()
        customerRepository.findById(uuid) >> null

        when:
        customerService.findById(uuid)

        then:
        thrown(RuntimeException)

    }

    def "should create a user"() {
        given:
        def customerRequest = buildDefaultCustomerRequest()

        customerRepository.createCustomer(customerRequest.name(), customerRequest.email()) >> buildDefaultCustomer()

        when:
        def newCustomer = customerService.createCustomer(customerRequest)

        then:
        newCustomer != null

    }

    def "should update user balance and return with new balance"() {
        given:
        def amount = BigDecimal.TEN
        def customer = buildDefaultCustomer()
        def updatedCustomer = buildDefaultCustomer()
        updatedCustomer.setBalance(updatedCustomer.balance.add(amount))

        customerRepository.findById(customer.id) >> customer
        customerRepository.updateCustomerBalance(customer.id, customer.balance.add(amount)) >> updatedCustomer

        when:
        def response = customerService.addUserBalance(customer.id, amount)

        then:
        response != null
        response.balance() == updatedCustomer.balance
    }

    private static CustomersRecord buildDefaultCustomer() {
        return new CustomersRecord(
                UUID.randomUUID(),
                "Name Test",
                "email test",
                BigDecimal.ONE,
                LocalDateTime.now(),
                LocalDateTime.now()
        )
    }

    private static CustomerRequest buildDefaultCustomerRequest() {
        return new CustomerRequest(
                "Name Test",
                "email test"
        )
    }

}
