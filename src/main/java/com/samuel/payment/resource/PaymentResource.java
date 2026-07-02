package com.samuel.payment.resource;

import com.samuel.payment.domain.mapper.PaymentMapper;
import com.samuel.payment.domain.request.PaymentRequest;
import com.samuel.payment.domain.response.PaymentResponse;
import com.samuel.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentResource {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<Void> createPayment(@RequestBody PaymentRequest request) {
        paymentService.executePayment(request);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/{id}/{startDate}")
    public ResponseEntity<List<PaymentResponse>> findLastPayments(
            @PathVariable UUID id, @PathVariable LocalDate startDate) {
        var paymentList = paymentService.fetchLastPayments(id, startDate);

        var response = PaymentMapper.toPaymentResponseList(paymentList);
        return ResponseEntity.ok(response);
    }


}
