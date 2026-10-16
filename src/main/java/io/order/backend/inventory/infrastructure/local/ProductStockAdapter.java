package io.order.backend.inventory.infrastructure.local;

import io.order.backend.inventory.application.port.in.InventoryUseCase;
import io.order.backend.inventory.application.port.out.StockPort;
import io.order.backend.product.application.port.in.ProductStockUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ProductStockAdapter implements StockPort {
    private final ProductStockUseCase products;

    @Override
    public Map<String, InventoryUseCase.Snapshot> reserve(Map<String, Integer> quantities) {
        var result = new LinkedHashMap<String, InventoryUseCase.Snapshot>();
        products.reserve(quantities).forEach((id, p) -> result.put(id, new InventoryUseCase.Snapshot(
                p.id(), p.sellerId(), p.title(), p.sku(), p.price(), p.deleted())));
        return result;
    }

    @Override
    public void settle(List<InventoryUseCase.Line> lines, String target) {
        products.settle(lines.stream().map(l -> new ProductStockUseCase.Line(l.productId(), l.quantity())).toList(), target);
    }
}
