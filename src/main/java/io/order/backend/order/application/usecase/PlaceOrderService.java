package io.order.backend.order.application.usecase;

import io.order.backend.order.application.port.in.PlaceOrderUseCase;
import io.order.backend.order.application.port.out.OrderRepository;
import io.order.backend.order.domain.Order;
import io.order.backend.order.domain.OrderItem;
import io.order.backend.order.application.port.in.OrderResult;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PlaceOrderService implements PlaceOrderUseCase {
    private final OrderRepository orders;

    @Override
    @Transactional
    public OrderResult placeOrder(CreateOrder command) {
        var items = command.lines().stream().map(snapshot -> {
            var line = new OrderItem();
            line.setProductId(snapshot.productId());
            line.setProductName(snapshot.productName());
            line.setSku(snapshot.sku());
            line.setQuantity(snapshot.quantity());
            line.setUnitPrice(snapshot.unitPrice());
            line.setSubtotal(snapshot.subtotal());
            return line;
        }).toList();
        var order = Order.place(command.ownerId(), command.sellerId(), items, command.subtotal(),
                command.shippingAddress(), command.notes(), Instant.now(), LocalDateTime.now());
        return OrderResult.from(orders.save(order));
    }
}
