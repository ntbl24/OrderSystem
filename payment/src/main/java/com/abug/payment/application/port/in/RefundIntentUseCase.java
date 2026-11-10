package com.abug.payment.application.port.in;

public interface RefundIntentUseCase {
    void acknowledge(String paymentId);
}
