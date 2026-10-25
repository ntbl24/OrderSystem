package io.order.backend.product.application.port.in;

import io.order.backend.product.domain.ProductId;

public interface ProductCommandUseCase {
    void deleteProduct(ProductId id);
}
