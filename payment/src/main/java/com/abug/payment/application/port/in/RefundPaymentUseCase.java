package com.abug.payment.application.port.in;

import com.abug.payment.domain.PaymentId;

public interface RefundPaymentUseCase {
    PaymentResult refund(PaymentId id, String key, String reason);
}
