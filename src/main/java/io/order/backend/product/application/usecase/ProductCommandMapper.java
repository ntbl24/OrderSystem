package io.order.backend.product.application.usecase;

import io.order.backend.product.application.port.in.ProductCommand;
import io.order.backend.product.domain.Product;
import io.order.backend.product.domain.ProductDetails;

final class ProductCommandMapper {

    ProductCommandMapper(){}

    static Product toDomain(ProductCommand command) {
        return Product.create(
            command.title(),
            command.category(),
            command.description(),
            command.price(),
            command.stock(),
            command.sku(),
            command.sellerId()
        );
    }

     static ProductDetails toDetails(ProductCommand command) {
        return new ProductDetails(
            command.title(),
            command.category(),
            command.description(),
            command.price(),
            command.stock(),
            command.sku(),
            command.sellerId()
        );
    }
}
