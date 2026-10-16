package io.order.backend.inventory.application.port.out;

import io.order.backend.inventory.application.port.in.InventoryUseCase;
import java.util.List;
import java.util.Map;

public interface StockPort {
    Map<String, InventoryUseCase.Snapshot> reserve(Map<String, Integer> quantities);
    void settle(List<InventoryUseCase.Line> lines, String target);
}
