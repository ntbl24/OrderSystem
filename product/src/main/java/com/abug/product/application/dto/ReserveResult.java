package com.abug.product.application.dto;

import com.abug.product.application.port.in.ProductStockUseCase;
import com.abug.product.domain.OrderId;
import java.util.List;

public record ReserveResult(
        OrderId orderId,
        List<ProductStockUseCase.Snapshot> items
) {
}
