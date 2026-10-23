package io.order.backend.order.application.usecase;

import io.order.backend.common.application.port.CurrentBuyerPort;
import io.order.backend.common.exception.ResourceNotFoundException;
import io.order.backend.order.application.port.out.InventoryPort;
import io.order.backend.order.application.port.in.OrderUseCase;
import io.order.backend.order.domain.Order;
import io.order.backend.order.application.port.in.OrderResult;
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
        if (!buyer.buyerId().equals(order.getUserId()))
            throw new ResourceNotFoundException("Order not found");
        return order;
    }

    public OrderResult getOrderById(String id) {
        return OrderResult.from(owned(orders.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found"))));
    }

    public OrderResult getOrderByOrderNumber(String number) {
        return OrderResult.from(owned(
                orders.findByOrderNumber(number).orElseThrow(() -> new ResourceNotFoundException("Order not found"))));
    }

    public List<OrderResult> getOrdersByUserId(String userId) {
        if (!buyer.buyerId().equals(userId))
            throw new ResourceNotFoundException("Orders not found");
        return orders.findByUserId(userId).stream().map(OrderResult::from).toList();
    }

    @Transactional
    public OrderResult cancel(String id) {
        Order order = owned(orders.lockById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found")));
        order.cancel();
        InventorySettlement.settle(order, inventory, "RELEASED");
        return OrderResult.from(orders.save(order));
    }
}
