package com.abug.order.application.usecase;

import com.abug.order.application.port.out.InventoryPort;
import com.abug.order.domain.Order;

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
