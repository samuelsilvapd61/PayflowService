package com.samuel.payment.resource;

import com.samuel.payment.domain.request.AddUserBalanceRequest;
import com.samuel.payment.domain.request.CustomerRequest;
import com.samuel.payment.domain.response.CustomerResponse;
import com.samuel.payment.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/customers")
public class CustomerResource {

    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(@RequestBody CustomerRequest request) {
        var newCustomer = customerService.createCustomer(request);

        var uri = URI.create("/customers/" + newCustomer.id());
        return ResponseEntity.created(uri).body(newCustomer);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> findById(@PathVariable UUID id) {
        var customer = customerService.findById(id);

        return ResponseEntity.ok(customer);
    }

    @PostMapping("/{id}")
    public ResponseEntity<CustomerResponse> addUserBalance(
            @PathVariable UUID id, @RequestBody AddUserBalanceRequest request) {
        var customer = customerService.addUserBalance(id, request.amount());

        return ResponseEntity.ok(customer);
    }

}
