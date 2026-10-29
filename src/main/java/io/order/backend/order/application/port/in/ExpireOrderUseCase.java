package io.order.backend.order.application.port.in;

import io.order.backend.order.domain.OrderId;

public interface ExpireOrderUseCase {
    void expire(OrderId id);
}
