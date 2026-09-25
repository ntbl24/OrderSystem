package io.order.backend.product.infrastructure;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import io.order.backend.product.domain.Product;

public interface ProductRepository {
    Optional<Product> findById(String productId);
    int decreaseStock(Long productId, int quantity);
    boolean existsBySku(String sku);
    Product save(Product product);
    boolean existsById(String id);
    void deleteById(String id);
    Optional<Product> findBySku(String sku);
    List<Product> findAll();
    Page<Product> findAll(Pageable pageable);
    List<Product> findByCategory(String category);
}
