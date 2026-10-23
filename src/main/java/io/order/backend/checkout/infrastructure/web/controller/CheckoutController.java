package io.order.backend.checkout.infrastructure.web.controller;

import io.order.backend.checkout.application.port.in.CheckoutUseCase;
import io.order.backend.checkout.infrastructure.web.dto.CheckoutRequest;
import io.order.backend.order.infrastructure.web.dto.OrderResponse;
import io.order.backend.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/checkouts")
@RequiredArgsConstructor
public class CheckoutController {
    private final CheckoutUseCase service;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> checkout(@RequestHeader("Idempotency-Key") String key,
            @Valid @RequestBody CheckoutRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(OrderResponse.from(service.checkout(key, request.toCommand()))));
    }
}
