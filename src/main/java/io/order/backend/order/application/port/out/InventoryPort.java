package io.order.backend.order.application.port.out;

import java.util.List;

public interface InventoryPort {
    void settle(List<Line> lines, String target);

    record Line(String productId, int quantity) {
    }
}
