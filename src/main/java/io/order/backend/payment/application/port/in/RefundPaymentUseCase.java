package io.order.backend.payment.application.port.in;

public interface RefundPaymentUseCase {
    PaymentResult refund(String id, String key, String reason);
}
