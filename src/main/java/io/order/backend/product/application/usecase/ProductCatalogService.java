package io.order.backend.product.application.usecase;

import io.order.backend.product.application.port.in.ProductCatalogQuery;
import io.order.backend.product.application.port.out.ProductRepository;
import io.order.backend.product.domain.ProductId;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductCatalogService implements ProductCatalogQuery {
    private final ProductRepository products;

    @Override
    public Optional<Snapshot> findById(ProductId id) {
        return products.findById(id).map(product -> new Snapshot(product.getId(), product.getSellerId(),
                product.getTitle(), product.getSku(), product.getPrice(), product.isDeleted()));
    }
    @Override
    public java.util.Map<ProductId, Snapshot> findByIds(java.util.Collection<ProductId> ids) {
        java.util.Map<ProductId, Snapshot> result = new java.util.LinkedHashMap<>();
        products.findAllById(ids).forEach(product -> result.put(product.getId(),
                new Snapshot(product.getId(), product.getSellerId(), product.getTitle(), product.getSku(),
                        product.getPrice(), product.isDeleted())));
        return java.util.Map.copyOf(result);
    }
}
