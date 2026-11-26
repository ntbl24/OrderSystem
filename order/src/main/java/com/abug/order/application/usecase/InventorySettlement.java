package com.abug.order.application.usecase;

import com.abug.order.application.port.out.ProductStockPort;
import com.abug.order.domain.Order;
import com.abug.order.domain.ReservationStatus;
import org.springframework.stereotype.Component;

@Component
public class InventorySettlement {
    private InventorySettlement() {
    }

    static void settle(Order order, ProductStockPort port, ReservationStatus target) {
        if (!order.requiresReservationTransition(target))
            return;
        port.settle(order.getId(), order.getItems().stream()
                .map(line -> new ProductStockPort.Line(line.getProductId(), line.getQuantity())).toList(), target.name());
        order.transitionReservation(target);
    }
}
