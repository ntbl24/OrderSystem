package com.abug.product.application.port.in;

import com.abug.product.domain.ProductId;

public interface ProductCommandUseCase {
    void deleteProduct(ProductId id);
}
