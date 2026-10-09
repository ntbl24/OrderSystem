package io.order.backend.payment.api;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.order.backend.common.response.ApiResponse;

import io.order.backend.payment.application.PaymentService;
import io.order.backend.payment.dto.PaymentDTO;
import io.order.backend.payment.dto.QueryPaymentResponseRawDTO;
import jakarta.validation.Valid;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentDTO>> createPayment(@Valid @RequestBody PaymentDTO paymentDTO) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.createPayment(paymentDTO)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentDTO>> updatePayment(
            @PathVariable String id,
            @Valid @RequestBody PaymentDTO paymentDTO) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.upadtePayment(id, paymentDTO)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePayment(@PathVariable String id) {
        paymentService.deletePayment(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentDTO>> getPaymentById(@PathVariable String id) {
        return paymentService.getPaymentById(id)
                .map(ApiResponse::success)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<PaymentDTO>> getPaymentByOrderId(@PathVariable String orderId) {
        return paymentService.getPaymentByOrderId(orderId)
                .map(ApiResponse::success)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/intent/{paymentIntentId}")
    public ResponseEntity<ApiResponse<PaymentDTO>> getPaymentByPaymentIntentId(@PathVariable String paymentIntentId) {
        return paymentService.getPaymentByPaymentIntentId(paymentIntentId)
                .map(ApiResponse::success)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/process")
    public ResponseEntity<ApiResponse<PaymentDTO>> processPayment(@Valid @RequestBody PaymentDTO paymentDTO) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.processPayment(paymentDTO)));
    }

    @PostMapping("/{id}/refund")
    public ResponseEntity<ApiResponse<PaymentDTO>> refundPayment(
            @PathVariable String id,
            @RequestParam String reason) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.refundPayment(id, reason)));
    }

    @GetMapping("/{id}/get-status")
    public ResponseEntity<ApiResponse<QueryPaymentResponseRawDTO>> getPaymentStatus(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.queryPaymentStatus(id)));
    }

    @GetMapping("/callback/vnpay")
    public ResponseEntity<?> handleVnPayCallback(@RequestParam Map<String, String> params) throws Exception {
        try {
            paymentService.handleCallback(params);
            return ResponseEntity.ok(ApiResponse.success("Payment callback success", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Payment callback failed"));
        }
    }
} 