package com.abug.order.application.port.out;

import java.util.List;

import com.abug.product.domain.ProductId;

public interface InventoryPort {
    void settle(List<Line> lines, String target);

    record Line(ProductId productId, int quantity) {
    }
}
