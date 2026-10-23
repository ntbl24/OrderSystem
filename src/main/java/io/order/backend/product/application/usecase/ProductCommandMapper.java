package io.order.backend.product.application.usecase;

import io.order.backend.product.application.port.in.ProductCommand;
import io.order.backend.product.domain.Product;

final class ProductCommandMapper {
    private ProductCommandMapper() {}

    static Product toDomain(ProductCommand command) {
        Product result = new Product();
        result.setTitle(command.title());
        result.setCategory(command.category());
        result.setDescription(command.description());
        result.setPrice(command.price());
        result.setStock(command.stock());
        result.setSku(command.sku());
        result.setSellerId(command.sellerId());
        return result;
    }
}
