package io.order.backend.product.application.port.in;

import io.order.backend.product.domain.Product;
import io.order.backend.product.domain.ProductId;

public interface UpdateProductUseCase {
    Product updateProduct(ProductId id, ProductCommand command);

    void updateStock(ProductId id, int quantity);
}
