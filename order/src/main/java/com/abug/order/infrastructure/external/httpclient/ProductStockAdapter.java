package com.abug.order.infrastructure.external.httpclient;

import com.abug.order.application.port.out.ProductStockPort;
import com.abug.order.domain.valueobject.OrderId;
import com.abug.order.infrastructure.external.httpclient.dto.SettleStockRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductStockAdapter implements ProductStockPort {
    private final ProductClient productClient;

    @Override
    public void settle(OrderId orderId, List<Line> lines, String target) {
        List<SettleStockRequest.Item> items = lines.stream().map(line -> new SettleStockRequest.Item(line.productId().toString(), line.quantity())).toList();
        SettleStockRequest request = new SettleStockRequest(
                orderId.toString(),
                items,
                target);
        productClient.settle(request);
    }
}
