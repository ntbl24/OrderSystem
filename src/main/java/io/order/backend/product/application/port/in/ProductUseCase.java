package io.order.backend.product.application.port.in;

import io.order.backend.product.domain.Product;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductUseCase {
    Product createProduct(ProductCommand command);
    Product updateProduct(String id, ProductCommand command);
    void deleteProduct(String id);
    Product getProductById(String id);
    Product getProductBySku(String sku);
    List<Product> getAllProducts();
    Page<Product> getAllProducts(Pageable pageable);
    List<Product> getProductsByCategory(String category);
    void updateStock(String id, int quantity);
}
