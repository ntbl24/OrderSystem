package io.order.backend.product.infrastructure.persistence;

import io.order.backend.product.domain.Product;
import io.order.backend.product.domain.ProductId;
import java.util.Objects;

public final class ProductPersistenceMapper {
    private ProductPersistenceMapper() {}

    public static Product domain(ProductEntity e) {
        if (e == null) {
            return null;
        }
        return Product.restore(
                e.getId() != null ? new ProductId(e.getId()) : null,
                e.isDeleted(),
                e.getTitle(),
                e.getCategory(),
                e.getDescription(),
                e.getPrice(),
                e.getStock(),
                e.getSku(),
                e.getSellerId(),
                e.getReserved(),
                e.getVersion()
        );
    }

    public static void copy(Product d, ProductEntity e) {
        Objects.requireNonNull(d, "Domain product cannot be null");
        Objects.requireNonNull(e, "Entity cannot be null");
        e.update(d.getId().value(), d.getTitle(), d.getCategory(), d.getDescription(), d.getPrice(), d.getStock(), d.getSku(), d.getSellerId(), d.getReserved());
    }

    public static ProductEntity toEntity(Product d) {
        if (d == null) {
            return null;
        }
        ProductEntity e = new ProductEntity();
        copy(d, e);
        return e;
    }
}
