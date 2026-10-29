package io.order.backend.payment.application.port.in;

public interface PaymentCallbackUseCase {
    NotificationResult ipn(PaymentNotification notification);

    ReturnView returnView(PaymentNotification notification);

    enum NotificationResult {
        INVALID_SIGNATURE, NOT_FOUND, INVALID_DATA, CONFLICTING_TRANSACTION, ALREADY_CONFIRMED, CONFIRMED
    }

    record ReturnView(boolean verified, String status, String message) {
    }
}
