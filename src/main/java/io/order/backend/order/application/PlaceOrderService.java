package io.order.backend.order.application;

import io.order.backend.order.application.port.in.PlaceOrderUseCase;
import io.order.backend.order.application.port.out.OrderRepository;
import io.order.backend.order.domain.Order;
import io.order.backend.order.domain.OrderItem;
import io.order.backend.order.dto.OrderDTO;
import io.order.backend.order.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlaceOrderService implements PlaceOrderUseCase {
    private final OrderRepository orders;
    private final OrderMapper mapper;

    @Override
    @Transactional
    public OrderDTO placeOrder(CreateOrder command) {
        Order order = new Order();
        order.setId(UUID.randomUUID().toString());
        order.setOrderNumber("ORD-" + order.getId());
        order.setUserId(command.ownerId());
        order.setSellerId(command.sellerId());
        order.setItems(command.lines().stream().map(snapshot -> {
            var line = new OrderItem();
            line.setProductId(snapshot.productId());
            line.setProductName(snapshot.productName());
            line.setSku(snapshot.sku());
            line.setQuantity(snapshot.quantity());
            line.setUnitPrice(snapshot.unitPrice());
            line.setSubtotal(snapshot.subtotal());
            return line;
        }).toList());
        order.setSubtotal(command.subtotal());
        order.setShippingCost(BigDecimal.valueOf(30000));
        order.setShippingAddress(command.shippingAddress());
        order.setNotes(command.notes());
        order.setPaymentMethod("VNPAY");
        order.setPaymentStatus("UNPAID");
        order.setOrderStatus("UNPAID");
        order.setReservationStatus("HELD");
        order.setOrderDate(LocalDateTime.now());
        order.setExpiresAt(Instant.now().plusSeconds(900));
        return mapper.toDto(orders.save(order));
    }
}
