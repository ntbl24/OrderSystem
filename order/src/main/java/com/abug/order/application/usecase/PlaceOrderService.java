package com.abug.order.application.usecase;

import com.abug.order.application.port.in.PlaceOrderUseCase;
import com.abug.order.application.port.out.OrderRepository;
import com.abug.order.domain.Order;
import com.abug.order.domain.OrderItem;

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
    public Order placeOrder(CreateOrder command) {
        var items = command.lines().stream().map(snapshot -> {
            return new OrderItem(
                snapshot.productId(),
                snapshot.productName(),
                snapshot.sku(),
                snapshot.quantity(),
                snapshot.unitPrice()
            );
        }).toList();

        var order = Order.place(command.ownerId(), command.sellerId(), items, command.subtotal(),
                command.shippingAddress(), command.notes(), Instant.now(), LocalDateTime.now());
        return orders.save(order);
    }
}
