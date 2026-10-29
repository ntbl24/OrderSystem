package io.order.backend.order.application.usecase;

import io.order.backend.common.application.port.CurrentBuyerPort;
import io.order.backend.common.exception.ResourceNotFoundException;
import io.order.backend.order.application.port.out.InventoryPort;
import io.order.backend.order.application.port.in.OrderUseCase;
import io.order.backend.order.domain.Order;
import io.order.backend.order.domain.OrderId;
import io.order.backend.order.domain.OrderNumber;
import io.order.backend.user.domain.UserId;
import io.order.backend.order.application.port.out.OrderRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService implements OrderUseCase {
    private final OrderRepository orders;
    private final CurrentBuyerPort buyer;
    private final InventoryPort inventory;

    private Order owned(Order order) {
        if (!buyer.buyerId().equals(order.getUserId().toString()))
            throw new ResourceNotFoundException("Order not found");
        return order;
    }

    public Order getOrderById(OrderId id) {
        return owned(orders.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found")));
    }

    public Order getOrderByOrderNumber(OrderNumber number) {
        return owned(
                orders.findByOrderNumber(number).orElseThrow(() -> new ResourceNotFoundException("Order not found")));
    }

    public List<Order> getOrdersByUserId(UserId userId) {
        if (!buyer.buyerId().equals(userId.toString()))
            throw new ResourceNotFoundException("Orders not found");
        return orders.findByUserId(userId);
    }

    @Transactional
    public Order cancel(OrderId id) {
        Order order = owned(orders.lockById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found")));
        order.cancel();
        InventorySettlement.settle(order, inventory, "RELEASED");
        return orders.save(order);
    }
}
