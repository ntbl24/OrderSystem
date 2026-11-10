package com.abug.product.infrastructure.web.dto;

import com.abug.product.application.port.in.ReserveResult;

import java.math.BigDecimal;
import java.util.List;

public record ReserveStockResponse(
        String orderId,
        List<ReservedItem> items
) {
    public record ReservedItem(
            String productId,
            int quantity,
            String title,
            String sku,
            BigDecimal unitPrice,
            String sellerId
    ) {}

    public static ReserveStockResponse from(ReserveResult reserveResult){
        List<ReservedItem> items = reserveResult.items().stream().map(
                item -> new ReservedItem(item.id().toString(), item.quantity(), item.title(), item.sku(), item.price(), item.sellerId())
        ).toList();
        return new ReserveStockResponse(reserveResult.orderId().toString(), items);
    }
}
