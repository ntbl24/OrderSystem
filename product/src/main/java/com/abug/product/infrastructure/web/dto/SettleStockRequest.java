package com.abug.product.infrastructure.web.dto;

import com.abug.product.application.port.in.ProductStockUseCase;
import com.abug.product.application.port.in.command.SettleCommand;
import com.abug.product.common.constant.StockAction;
import com.abug.product.domain.OrderId;
import com.abug.product.domain.ProductId;

import java.util.List;

public record SettleStockRequest(
        String orderId,
        List<Item> items,
        StockAction action
) {
    public record Item(
            String productId,
            int quantity
    ) {}

    public SettleCommand toCommand(){
        return new SettleCommand(
                OrderId.of(orderId),
                items.stream().map(item -> new ProductStockUseCase.Line(ProductId.of(item.productId), item.quantity))
                        .toList(),
                action
        );
    }
}
