package io.order.backend.payment.infrastructure.web.controller;

import io.order.backend.common.response.ApiResponse;
import io.order.backend.payment.application.port.in.PaymentUseCase;
import io.order.backend.payment.infrastructure.web.dto.PaymentResponse;
import io.order.backend.payment.infrastructure.web.dto.CreatePaymentRequest;
import io.order.backend.payment.infrastructure.web.dto.RefundPaymentRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {
    private final PaymentUseCase service;

    public PaymentController(PaymentUseCase service) {
        this.service = service;
    }

    @PostMapping({ "", "/attempts", "/process" })
    public ResponseEntity<ApiResponse<PaymentResponse>> create(@Valid @RequestBody CreatePaymentRequest request,
            @RequestHeader("Idempotency-Key") String key, HttpServletRequest http) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(PaymentResponse.from(service.create(request.orderId(), validKey(key), http.getRemoteAddr()))));
    }

    @GetMapping("/{id}")
    public ApiResponse<PaymentResponse> get(@PathVariable String id) {
        return ApiResponse.success(PaymentResponse.from(service.get(id)));
    }

    @PostMapping({ "/{id}/refunds", "/{id}/refund" })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PaymentResponse>> refund(@PathVariable String id,
            @RequestHeader("Idempotency-Key") String key, @Valid @RequestBody RefundPaymentRequest request) {
        return ResponseEntity.accepted().body(ApiResponse.success(PaymentResponse.from(service.refund(id, validKey(key), request.reason()))));
    }

    @RequestMapping(value = "/{id}", method = { RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.DELETE })
    public ResponseEntity<Void> removed(@PathVariable String id) {
        return ResponseEntity.status(HttpStatus.GONE).build();
    }

    private static String validKey(String key) {
        if (key == null || key.isBlank() || key.length() > 128)
            throw new IllegalArgumentException("Invalid idempotency key");
        return key;
    }

}
