package io.order.backend.product.application.port.out;

import java.util.*;
import org.springframework.data.domain.*;
import io.order.backend.product.domain.Product;
import io.order.backend.product.domain.ProductId;

public interface ProductRepository {
    Optional<Product> findById(ProductId id);

    default Optional<Product> findById(String id) { return findById(ProductId.of(id)); }

    List<Product> findAllById(Collection<ProductId> ids);

    Optional<Product> lockById(String id);

    boolean existsBySku(String sku);

    Product save(Product product);

    boolean existsById(String id);

    void deleteById(String id);

    Optional<Product> findBySku(String sku);

    List<Product> findAll();

    Page<Product> findAll(Pageable pageable);

    List<Product> findByCategory(String category);
}
