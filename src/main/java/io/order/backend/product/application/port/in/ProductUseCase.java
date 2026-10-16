package io.order.backend.product.application.port.in;

import io.order.backend.product.dto.ProductDTO;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductUseCase {
    ProductDTO createProduct(ProductDTO dto);
    ProductDTO updateProduct(String id, ProductDTO dto);
    void deleteProduct(String id);
    ProductDTO getProductById(String id);
    ProductDTO getProductBySku(String sku);
    List<ProductDTO> getAllProducts();
    Page<ProductDTO> getAllProducts(Pageable pageable);
    List<ProductDTO> getProductsByCategory(String category);
    void updateStock(String id, int quantity);
}
