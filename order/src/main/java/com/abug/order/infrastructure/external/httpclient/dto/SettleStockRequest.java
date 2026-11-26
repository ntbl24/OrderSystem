package com.abug.order.infrastructure.external.httpclient.dto;

import java.util.List;

public record SettleStockRequest(
        String orderId,
        List<Item> items,
        String action
) {
    public record Item(
            String productId,
            int quantity
    ) {}
}
