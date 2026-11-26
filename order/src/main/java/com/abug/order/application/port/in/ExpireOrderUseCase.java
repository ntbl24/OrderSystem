package com.abug.order.application.port.in;

import com.abug.order.domain.valueobject.OrderId;

public interface ExpireOrderUseCase {
    void expire(OrderId id);
}
