package io.order.backend.order.application;

import io.order.backend.common.application.port.CurrentBuyerPort;
import io.order.backend.common.exception.ResourceNotFoundException;
import io.order.backend.order.application.port.out.InventoryPort;
import io.order.backend.order.application.port.in.OrderUseCase;
import io.order.backend.order.domain.Order;
import io.order.backend.order.dto.OrderDTO;
import io.order.backend.order.application.port.out.OrderRepository;
import io.order.backend.order.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService implements OrderUseCase {
    private final OrderRepository orders;
    private final OrderMapper mapper;
    private final CurrentBuyerPort buyer;
    private final InventoryPort inventory;

    private Order owned(Order order) {
        if (!buyer.buyerId().equals(order.getUserId()))
            throw new ResourceNotFoundException("Order not found");
        return order;
    }

    public OrderDTO getOrderById(String id) {
        return mapper
                .toDto(owned(orders.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found"))));
    }

    public OrderDTO getOrderByOrderNumber(String number) {
        return mapper.toDto(owned(
                orders.findByOrderNumber(number).orElseThrow(() -> new ResourceNotFoundException("Order not found"))));
    }

    public List<OrderDTO> getOrdersByUserId(String userId) {
        if (!buyer.buyerId().equals(userId))
            throw new ResourceNotFoundException("Orders not found");
        return mapper.toOrderDTOList(orders.findByUserId(userId));
    }

    @Transactional
    public OrderDTO cancel(String id) {
        Order order = owned(orders.lockById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found")));
        order.cancel();
        InventorySettlement.settle(order, inventory, "RELEASED");
        return mapper.toDto(orders.save(order));
    }
}
