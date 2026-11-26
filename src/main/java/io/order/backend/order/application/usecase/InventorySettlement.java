package io.order.backend.order.application.usecase;

import io.order.backend.order.application.port.out.InventoryPort;
import io.order.backend.order.domain.Order;
import io.order.backend.order.domain.ReservationStatus;

final class InventorySettlement {
    private InventorySettlement() {
    }

    static void settle(Order order, InventoryPort inventory, ReservationStatus target) {
        if (!order.requiresReservationTransition(target))
            return;
        inventory.settle(order.getItems().stream()
                .map(line -> new InventoryPort.Line(line.getProductId(), line.getQuantity())).toList(), target.name());
        order.transitionReservation(target);
    }
}
