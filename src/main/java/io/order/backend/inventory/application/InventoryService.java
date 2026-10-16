package io.order.backend.inventory.application;

import io.order.backend.inventory.application.port.in.InventoryUseCase;
import io.order.backend.inventory.application.port.out.StockPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import java.util.*;

@Service
@RequiredArgsConstructor
public class InventoryService implements InventoryUseCase {
    private final StockPort stock;

    @Override
    @Transactional
    public Map<String, Snapshot> reserve(Map<String, Integer> quantities) {
        return stock.reserve(quantities);
    }

    @Override
    @Transactional
    public void settle(List<Line> lines, String target) {
        stock.settle(lines, target);
    }
}
