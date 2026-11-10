package com.abug.product.application.port.in;

import com.abug.product.domain.Product;
import com.abug.product.domain.ProductId;
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
