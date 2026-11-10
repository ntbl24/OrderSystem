package com.abug.product.application.port.in;

import java.math.BigDecimal;
import com.abug.product.domain.ProductId;

public interface ProductStockUseCase {
    ReserveResult reserve(ReserveCommand command);
    void settle(SettleCommand command);

    record Snapshot(ProductId id, int quantity, String sellerId, String title, String sku, BigDecimal price, boolean deleted) {}
    record Line(ProductId productId, int quantity) {}

}
