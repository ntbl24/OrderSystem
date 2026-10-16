package io.order.backend.order.application;

import io.order.backend.order.application.port.in.PlaceOrderUseCase.CreateOrder;
import io.order.backend.order.application.port.in.PlaceOrderUseCase.Line;
import io.order.backend.order.application.port.out.OrderRepository;
import io.order.backend.order.domain.Order;
import io.order.backend.order.dto.OrderDTO;
import io.order.backend.order.mapper.OrderMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlaceOrderServiceTest {
    @Mock OrderRepository orders;
    @Mock OrderMapper mapper;
    @InjectMocks PlaceOrderService service;

    @Test
    void placeOrder_whenSnapshotProvided_preservesCheckoutDefaults() {
        var line = new Line("product", "name", "sku", 2, new BigDecimal("50000"), new BigDecimal("100000"));
        var command = new CreateOrder("buyer", "seller", List.of(line), line.subtotal(), "address", "notes");
        var dto = new OrderDTO();
        when(orders.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(mapper.toDto(any())).thenReturn(dto);
        Instant before = Instant.now();

        assertThat(service.placeOrder(command)).isSameAs(dto);

        var saved = ArgumentCaptor.forClass(Order.class);
        verify(orders).save(saved.capture());
        var order = saved.getValue();
        assertThat(UUID.fromString(order.getId()).toString()).isEqualTo(order.getId());
        assertThat(order.getOrderNumber()).isEqualTo("ORD-" + order.getId());
        assertThat(order.getUserId()).isEqualTo("buyer");
        assertThat(order.getSellerId()).isEqualTo("seller");
        assertThat(order.getShippingAddress()).isEqualTo("address");
        assertThat(order.getNotes()).isEqualTo("notes");
        assertThat(order.getSubtotal()).isEqualByComparingTo("100000");
        assertThat(order.getShippingCost()).isEqualByComparingTo("30000");
        assertThat(order.getTotal()).isEqualByComparingTo("130000");
        assertThat(order.getPaymentMethod()).isEqualTo("VNPAY");
        assertThat(order.getPaymentStatus()).isEqualTo("UNPAID");
        assertThat(order.getOrderStatus()).isEqualTo("UNPAID");
        assertThat(order.getReservationStatus()).isEqualTo("HELD");
        assertThat(order.getOrderDate()).isNotNull();
        assertThat(order.getExpiresAt()).isBetween(before.plusSeconds(900), Instant.now().plusSeconds(900));
        assertThat(order.getItems()).singleElement().satisfies(item -> {
            assertThat(item.getProductId()).isEqualTo(line.productId());
            assertThat(item.getProductName()).isEqualTo(line.productName());
            assertThat(item.getSku()).isEqualTo(line.sku());
            assertThat(item.getQuantity()).isEqualTo(line.quantity());
            assertThat(item.getUnitPrice()).isEqualTo(line.unitPrice());
            assertThat(item.getSubtotal()).isEqualTo(line.subtotal());
        });
    }

    @Test
    void createOrder_whenSourceListChanges_keepsImmutableSnapshot() {
        var lines = new ArrayList<>(List.of(new Line("p", "name", "sku", 1, BigDecimal.ONE, BigDecimal.ONE)));
        var command = new CreateOrder("buyer", "seller", lines, BigDecimal.ONE, "address", null);
        lines.clear();
        assertThat(command.lines()).hasSize(1);
        assertThatThrownBy(() -> command.lines().clear()).isInstanceOf(UnsupportedOperationException.class);
    }
}
