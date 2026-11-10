package com.abug.payment.application.port.in;

import com.abug.payment.domain.PaymentId;

public interface PaymentQueryUseCase {
    PaymentResult get(PaymentId id);
}
