package com.abug.order.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import com.abug.order.application.port.in.PlaceOrderUseCase.CreateOrder;
import com.abug.order.application.port.in.PlaceOrderUseCase.Line;
import com.abug.order.application.port.out.OrderRepository;
import com.abug.order.domain.Order;
import com.abug.order.domain.OrderItem;
import com.abug.product.domain.ProductId;
import com.abug.user.domain.UserId;

@ExtendWith(MockitoExtension.class)
class PlaceOrderServiceTest {
    @Mock
    OrderRepository orders;
    @InjectMocks
    PlaceOrderService service;

    @Test
    void placeOrder_whenCheckoutSnapshotProvided_mapsLinesAndSavesOrder() {
        var productId = ProductId.of("product");
        var ownerId = UserId.of("00000000-0000-0000-0000-000000000001");
        var line = new Line(productId, "Product", "SKU", 2,
                new BigDecimal("50000"), new BigDecimal("100000"));
        var command = new CreateOrder(ownerId, "seller", List.of(line), line.subtotal(),
                "address", "notes");
        var expectedOrder = mock(Order.class);

        try (MockedStatic<Order> orderFactory = mockStatic(Order.class)) {
            orderFactory.when(() -> Order.place(eq(ownerId), eq("seller"), anyList(),
                    eq(line.subtotal()), eq("address"), eq("notes"),
                    any(Instant.class), any(LocalDateTime.class))).thenAnswer(invocation -> {
                        List<OrderItem> items = invocation.getArgument(2);
                        assertThat(items).singleElement().satisfies(item -> {
                            assertThat(item.getProductId()).isEqualTo(productId);
                            assertThat(item.getProductName()).isEqualTo("Product");
                            assertThat(item.getSku()).isEqualTo("SKU");
                            assertThat(item.getQuantity()).isEqualTo(2);
                            assertThat(item.getUnitPrice()).isEqualByComparingTo("50000");
                            assertThat(item.getSubtotal()).isEqualByComparingTo("100000");
                        });
                        return expectedOrder;
                    });
            when(orders.save(expectedOrder)).thenReturn(expectedOrder);

            assertThat(service.placeOrder(command)).isSameAs(expectedOrder);

            verify(orders).save(expectedOrder);
        }
    }
}
