package com.abug.payment.application.port.in;

import com.abug.payment.domain.PaymentId;

public interface PaymentWorkUseCase {
    void tick();

    void process(PaymentId id);
}
