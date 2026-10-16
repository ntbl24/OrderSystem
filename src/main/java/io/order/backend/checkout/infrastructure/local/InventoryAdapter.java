package io.order.backend.checkout.infrastructure.local;

import io.order.backend.checkout.application.port.out.InventoryPort;
import io.order.backend.inventory.application.port.in.InventoryUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class InventoryAdapter implements InventoryPort {
    private final InventoryUseCase inventory;

    @Override
    public Map<String, ProductSnapshot> reserve(Map<String, Integer> quantities) {
        var result = new LinkedHashMap<String, ProductSnapshot>();
        inventory.reserve(quantities).forEach((id, p) -> result.put(id,
                new ProductSnapshot(p.id(), p.sellerId(), p.title(), p.sku(), p.price())));
        return result;
    }
}
