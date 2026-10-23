package io.order.backend.payment.application.port.in;

public interface RefundIntentUseCase {
    void acknowledge(String paymentId);
}
