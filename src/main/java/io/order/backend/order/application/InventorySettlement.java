package io.order.backend.order.application;

import io.order.backend.order.application.port.out.InventoryPort;
import io.order.backend.order.domain.Order;

final class InventorySettlement {
    private InventorySettlement() {
    }

    static void settle(Order order, InventoryPort inventory, String target) {
        if (!order.requiresReservationTransition(target))
            return;
        inventory.settle(order.getItems().stream()
                .map(line -> new InventoryPort.Line(line.getProductId(), line.getQuantity())).toList(), target);
        order.transitionReservation(target);
    }
}
