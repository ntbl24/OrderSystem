package io.order.backend.payment.application.port.in;

public interface PaymentQueryUseCase {
    PaymentResult get(String id);
}
