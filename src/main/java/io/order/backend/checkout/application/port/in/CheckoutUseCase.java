package io.order.backend.checkout.application.port.in;

import io.order.backend.order.application.port.in.OrderResult;

public interface CheckoutUseCase {
    OrderResult checkout(String key, CheckoutCommand request);
}
