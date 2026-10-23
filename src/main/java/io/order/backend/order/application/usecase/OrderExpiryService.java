package io.order.backend.order.application.usecase;

import io.order.backend.order.application.port.out.OrderRepository;
import io.order.backend.order.application.port.out.InventoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class OrderExpiryService implements io.order.backend.order.application.port.in.ExpireOrderUseCase {
    private final OrderRepository orders;
    private final InventoryPort inventory;

    @Transactional
    public void expire(String id) {
        var order = orders.lockById(id).orElseThrow();
        if (order.expire(Instant.now())) {
            InventorySettlement.settle(order, inventory, "RELEASED");
            orders.save(order);
        }
    }
}
