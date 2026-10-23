package io.order.backend.payment.application.port.in;

public interface PaymentUseCase {
    PaymentResult create(String orderId, String key, String ipAddress);

    PaymentResult get(String id);

    PaymentResult refund(String id, String key, String reason);

    NotificationResult ipn(PaymentNotification notification);

    ReturnView returnView(PaymentNotification notification);

    enum NotificationResult {
        INVALID_SIGNATURE, NOT_FOUND, INVALID_DATA, CONFLICTING_TRANSACTION, ALREADY_CONFIRMED, CONFIRMED
    }

    record ReturnView(boolean verified, String status, String message) {
    }
}
