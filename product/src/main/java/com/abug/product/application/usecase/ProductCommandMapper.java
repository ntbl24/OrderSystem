package com.abug.product.application.usecase;

import com.abug.product.application.port.in.command.ProductCommand;
import com.abug.product.domain.Product;
import com.abug.product.domain.ProductDetails;

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
