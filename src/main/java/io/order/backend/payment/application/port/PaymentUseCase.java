package io.order.backend.payment.application.port;

import io.order.backend.payment.dto.PaymentResponse;

public interface PaymentUseCase {
    PaymentResponse create(String orderId, String key, String ipAddress);

    PaymentResponse get(String id);

    PaymentResponse refund(String id, String key, String reason);

    NotificationResult ipn(PaymentNotification notification);

    ReturnView returnView(PaymentNotification notification);

    enum NotificationResult {
        INVALID_SIGNATURE, NOT_FOUND, INVALID_DATA, CONFLICTING_TRANSACTION, ALREADY_CONFIRMED, CONFIRMED
    }

    record ReturnView(boolean verified, String status, String message) {
    }
}
