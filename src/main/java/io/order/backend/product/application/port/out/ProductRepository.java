package io.order.backend.product.application.port.out;

import java.util.*;
import org.springframework.data.domain.*;
import io.order.backend.product.domain.Product;

public interface ProductRepository {
    Optional<Product> findById(String id);

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
