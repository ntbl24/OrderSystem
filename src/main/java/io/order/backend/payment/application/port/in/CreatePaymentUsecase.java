package io.order.backend.payment.application.port.in;

public interface CreatePaymentUsecase {
    PaymentResult create(CreatePaymentCommand command);
}