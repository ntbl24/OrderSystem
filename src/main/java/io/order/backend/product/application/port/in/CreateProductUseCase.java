package io.order.backend.product.application.port.in;

import io.order.backend.product.domain.Product;

public interface CreateProductUseCase {
    Product createProduct(CreateProductCommand command);
}
