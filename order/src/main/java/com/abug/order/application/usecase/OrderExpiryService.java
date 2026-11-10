package com.abug.order.application.usecase;

import com.abug.order.application.port.out.OrderRepository;
import com.abug.order.domain.OrderId;
import com.abug.order.application.port.out.InventoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class OrderExpiryService implements com.abug.order.application.port.in.ExpireOrderUseCase {
    private final OrderRepository orders;
    private final InventoryPort inventory;

    @Transactional
    public void expire(OrderId id) {
        var order = orders.lockById(id).orElseThrow();
        if (order.expire(Instant.now())) {
            InventorySettlement.settle(order, inventory, "RELEASED");
            orders.save(order);
        }
    }
}
