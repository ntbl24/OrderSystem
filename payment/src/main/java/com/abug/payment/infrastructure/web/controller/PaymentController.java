package com.abug.payment.infrastructure.web.controller;

import com.abug.payment.application.port.in.*;
import com.abug.payment.common.exception.AppException;
import com.abug.payment.common.exception.ErrorCode;
import com.abug.payment.common.response.ApiResponse;
import com.abug.payment.domain.OrderId;
import com.abug.payment.domain.PaymentId;
import com.abug.payment.infrastructure.web.dto.PaymentResponse;
import com.abug.payment.infrastructure.web.dto.CreatePaymentRequest;
import com.abug.payment.infrastructure.web.dto.RefundPaymentRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PaymentController {
    private final CreatePaymentUsecase createPayments;
    private final PaymentQueryUseCase queries;
    private final RefundPaymentUseCase refunds;

    @PostMapping({ "", "/attempts", "/process" })
    public ApiResponse<PaymentResponse> create(@Valid @RequestBody CreatePaymentRequest request,
            @RequestHeader("Idempotency-Key") String key, HttpServletRequest http) {
        return ApiResponse.success(PaymentResponse.from(createPayments.create(new CreatePaymentCommand(OrderId.of(request.orderId()), validKey(key), http.getRemoteAddr()))));
    }

    @GetMapping("/{id}")
    public ApiResponse<PaymentResponse> get(@PathVariable String id) {
        return ApiResponse.success(PaymentResponse.from(queries.get(PaymentId.of(id))));
    }

    @PostMapping({ "/{id}/refunds", "/{id}/refund" })
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<PaymentResponse> refund(@PathVariable String id,
            @RequestHeader("Idempotency-Key") String key, @Valid @RequestBody RefundPaymentRequest request) {
        return ApiResponse.success(PaymentResponse.from(refunds.refund(PaymentId.of(id), validKey(key), request.reason())));
    }

    @RequestMapping(value = "/{id}", method = { RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.DELETE })
    public ApiResponse<Void> removed(@PathVariable String id) {
        return ApiResponse.success(null);
    }

    private static String validKey(String key) {
        if (key == null || key.isBlank() || key.length() > 128)
            throw new AppException(ErrorCode.INVALID_INPUT,"Invalid idempotency key");
        return key;
    }

}
