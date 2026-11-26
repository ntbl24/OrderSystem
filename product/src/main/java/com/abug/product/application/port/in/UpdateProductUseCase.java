package com.abug.product.application.port.in;

import com.abug.product.application.port.in.command.ProductCommand;
import com.abug.product.domain.Product;
import com.abug.product.domain.ProductId;

public interface UpdateProductUseCase {
    Product updateProduct(ProductId id, ProductCommand command);

    void updateStock(ProductId id, int quantity);
}
