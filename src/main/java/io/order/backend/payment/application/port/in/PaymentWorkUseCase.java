package io.order.backend.payment.application.port.in;

public interface PaymentWorkUseCase {
    void tick();

    void process(String id);
}
