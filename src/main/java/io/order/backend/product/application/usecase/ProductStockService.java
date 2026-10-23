package io.order.backend.product.application.usecase;

import io.order.backend.common.exception.ResourceNotFoundException;
import io.order.backend.product.application.port.in.ProductStockUseCase;
import io.order.backend.product.application.port.out.ProductRepository;
import io.order.backend.product.domain.Product;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductStockService implements ProductStockUseCase {
    private final ProductRepository products;

    @Override
    @Transactional
    public Map<String, Snapshot> reserve(Map<String, Integer> quantities) {
        Map<String, Product> locked = lockProducts(quantities.keySet());
        Map<String, Snapshot> result = new LinkedHashMap<>();
        locked.forEach((id, product) -> {
            if (product.isDeleted())
                throw new IllegalStateException("Product unavailable");
            product.validate();
            product.reserve(quantities.get(id));
            products.save(product);
            result.put(id, new Snapshot(product.getId().value().toString(), product.getSellerId(), product.getTitle(),
                    product.getSku(), product.getPrice(), product.isDeleted()));
        });
        return Map.copyOf(result);
    }

    @Override
    @Transactional
    public void settle(List<Line> lines, String target) {
        Map<String, Product> locked = lockProducts(lines.stream().map(Line::productId).toList());
        for (Line line : lines) {
            Product product = locked.get(line.productId());
            switch (target) {
                case "CONSUMED" -> product.consume(line.quantity());
                case "RELEASED" -> product.release(line.quantity());
                case "RETURNED" -> product.restock(line.quantity());
                default -> throw new IllegalStateException("Invalid inventory transition");
            }
            products.save(product);
        }
    }

    private Map<String, Product> lockProducts(Collection<String> ids) {
        Map<String, Product> result = new LinkedHashMap<>();
        ids.stream().distinct().sorted().forEach(id -> result.put(id, products.lockById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"))));
        return result;
    }
}
