package io.order.backend.checkout.application.port.in;

import io.order.backend.checkout.dto.CheckoutRequest;
import io.order.backend.order.dto.OrderDTO;

public interface CheckoutUseCase {
    OrderDTO checkout(String key, CheckoutRequest request);
}
