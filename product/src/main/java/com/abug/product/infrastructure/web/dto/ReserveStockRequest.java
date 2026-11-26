package com.abug.product.infrastructure.web.dto;

import com.abug.product.application.port.in.ProductStockUseCase;
import com.abug.product.application.port.in.command.ReserveCommand;
import com.abug.product.domain.OrderId;
import com.abug.product.domain.ProductId;

import java.util.List;

public record ReserveStockRequest(
        String orderId,
        List<Item> items
) {
    public record Item(
            String productId,
            int quantity
    ) {}

    public ReserveCommand toCommand(){
        return new ReserveCommand(OrderId.of(orderId), items.stream().map( item -> new ProductStockUseCase.Line(ProductId.of(item.productId), item.quantity)).toList());
    }
}