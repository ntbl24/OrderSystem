package io.order.backend.payment.application.port.in;

import io.order.backend.payment.domain.PaymentId;

public interface PaymentWorkUseCase {
    void tick();

    void process(PaymentId id);
}
