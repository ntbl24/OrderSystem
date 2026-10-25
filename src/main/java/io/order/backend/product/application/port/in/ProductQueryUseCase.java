package io.order.backend.product.application.port.in;

import io.order.backend.product.domain.Product;
import io.order.backend.product.domain.ProductId;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductQueryUseCase {
    Product getProductById(ProductId id);

    Product getProductBySku(String sku);

    List<Product> getAllProducts();

    Page<Product> getAllProducts(Pageable pageable);

    List<Product> getProductsByCategory(String category);
}
