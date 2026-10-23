package io.order.backend.cart.infrastructure.local;

import io.order.backend.cart.application.port.out.ProductCatalogPort;
import io.order.backend.product.application.port.in.ProductCatalogQuery;
import io.order.backend.product.domain.ProductId;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LocalProductCatalogAdapter implements ProductCatalogPort {
    private final ProductCatalogQuery catalog;

    @Override
    public Optional<Snapshot> findById(ProductId id) {
        return catalog.findById(id).map(product ->
                new Snapshot(product.id(), product.title(), product.price()));
    }
}
