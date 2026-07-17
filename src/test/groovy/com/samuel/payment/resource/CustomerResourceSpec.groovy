package com.samuel.payment.resource


import com.samuel.payment.domain.request.CustomerRequest
import com.samuel.payment.domain.response.CustomerResponse
import com.samuel.payment.service.CustomerService
import groovy.json.JsonSlurper
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

import java.nio.charset.StandardCharsets
import java.time.LocalDateTime

class CustomerResourceSpec extends Specification {

    MockMvc mvc

    CustomerService customerService = Mock()
    CustomerResource customerResource

    def setup() {
        customerResource = new CustomerResource(customerService)

        mvc = MockMvcBuilders
                .standaloneSetup(customerResource)
                .build()
    }

    def "should create customer"() {
        given:
        def id = UUID.randomUUID()
        def response = new CustomerResponse(
                id,
                "Samuel",
                "samuel@gmail.com",
                BigDecimal.ZERO,
                LocalDateTime.now(),
                LocalDateTime.now()
        )

        customerService.createCustomer(_ as CustomerRequest) >> response


        when:
        def mvcResponse = mvc.perform(
                MockMvcRequestBuilders.post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content("""
                        {
                            "name":"Samuel",
                            "email":"samuel@gmail.com"
                        }
                        """)
        ).andReturn()

        then:
        mvcResponse.response.status == HttpStatus.CREATED.value()

        def json = new JsonSlurper()
                .parseText(mvcResponse.response.contentAsString)

        json.id == id.toString()
        json.name == "Samuel"
        json.email == "samuel@gmail.com"
    }
}