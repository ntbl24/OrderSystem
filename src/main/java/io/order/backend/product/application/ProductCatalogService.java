package io.order.backend.product.application;

import io.order.backend.product.application.port.in.ProductCatalogQuery;
import io.order.backend.product.application.port.out.ProductRepository;
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
    public Optional<Snapshot> findById(String id) {
        return products.findById(id).map(product -> new Snapshot(product.getId(), product.getSellerId(),
                product.getTitle(), product.getSku(), product.getPrice(), product.isDeleted()));
    }
}
