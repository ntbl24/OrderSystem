package com.abug.product.application.port.in;

import com.abug.product.domain.OrderId;
import java.util.List;

public record ReserveResult(
        OrderId orderId,
        List<ProductStockUseCase.Snapshot> items
) {
}
