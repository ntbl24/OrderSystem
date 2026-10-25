package io.order.backend.product.application.port.out;

import io.order.backend.product.domain.Product;
import io.order.backend.product.domain.ProductId;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepository {
    Optional<Product> findById(ProductId id);

    List<Product> findAllById(Collection<ProductId> ids);

    Optional<Product> lockById(ProductId id);

    Product save(Product product);

    boolean existsById(ProductId id);

    void deleteById(ProductId id);

    Optional<Product> findBySku(String sku);

    boolean existsBySku(String sku);

    List<Product> findAll();

    Page<Product> findAll(Pageable pageable);

    List<Product> findByCategory(String category);

    default Optional<Product> findById(String id) {
        return findById(ProductId.of(id));
    }

    default Optional<Product> lockById(String id) {
        return lockById(ProductId.of(id));
    }

    default boolean existsById(String id) {
        return existsById(ProductId.of(id));
    }

    default void deleteById(String id) {
        deleteById(ProductId.of(id));
    }
}
