package com.abug.product.application.port.in;

import com.abug.product.application.port.in.command.ProductCommand;
import com.abug.product.domain.Product;

public interface CreateProductUseCase {
    Product createProduct(ProductCommand command);
}
