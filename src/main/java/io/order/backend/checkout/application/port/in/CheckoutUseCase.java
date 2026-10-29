package io.order.backend.checkout.application.port.in;

import io.order.backend.order.domain.Order;

public interface CheckoutUseCase {
    Order checkout(String key, CheckoutCommand request);
}
