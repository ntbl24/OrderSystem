package com.abug.payment.application.port.in;

public interface CreatePaymentUsecase {
    PaymentResult create(CreatePaymentCommand command);
}