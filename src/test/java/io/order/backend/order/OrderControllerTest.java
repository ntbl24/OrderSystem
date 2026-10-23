package io.order.backend.order;

import io.order.backend.order.application.port.in.OrderUseCase;
import io.order.backend.order.application.port.in.OrderResult;
import io.order.backend.order.infrastructure.web.controller.OrderController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {
    @Mock OrderUseCase orders;
    MockMvc mvc;
    OrderResult order;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new OrderController(orders)).build();
        order = new OrderResult();
        order.setId("order-id");
        order.setUserId("buyer");
        order.setSellerId("seller");
        order.setOrderNumber("ORD-order-id");
        order.setItems(List.of());
        order.setSubtotal(new BigDecimal("100000"));
        order.setShippingCost(new BigDecimal("30000"));
        order.setTotal(new BigDecimal("130000"));
        order.setExpiresAt(Instant.parse("2026-10-22T05:00:00Z"));
        order.setOrderDate(LocalDateTime.of(2026, 10, 22, 10, 0));
        order.setPaymentMethod("VNPAY");
        order.setPaymentStatus("UNPAID");
        order.setOrderStatus("UNPAID");
    }

    @Test
    void get_ownedOrder_preservesIdentifiersMoneyAndStatusFields() throws Exception {
        when(orders.getOrderById("order-id")).thenReturn(order);

        mvc.perform(get("/api/v1/orders/order-id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("order-id"))
                .andExpect(jsonPath("$.data.userId").value("buyer"))
                .andExpect(jsonPath("$.data.sellerId").value("seller"))
                .andExpect(jsonPath("$.data.orderNumber").value("ORD-order-id"))
                .andExpect(jsonPath("$.data.subtotal").value(100000))
                .andExpect(jsonPath("$.data.shippingCost").value(30000))
                .andExpect(jsonPath("$.data.total").value(130000))
                .andExpect(jsonPath("$.data.paymentMethod").value("VNPAY"))
                .andExpect(jsonPath("$.data.paymentStatus").value("UNPAID"))
                .andExpect(jsonPath("$.data.orderStatus").value("UNPAID"));
    }

    @Test
    void numberUserAndCancel_preserveRoutesAndResponseMapping() throws Exception {
        when(orders.getOrderByOrderNumber("ORD-order-id")).thenReturn(order);
        when(orders.getOrdersByUserId("buyer")).thenReturn(List.of(order));
        when(orders.cancel("order-id")).thenAnswer(call -> {
            order.setOrderStatus("CANCELLED");
            return order;
        });

        mvc.perform(get("/api/v1/orders/number/ORD-order-id"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.id").value("order-id"));
        mvc.perform(get("/api/v1/orders/user/buyer"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data[0].id").value("order-id"));
        mvc.perform(post("/api/v1/orders/order-id/cancel"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.orderStatus").value("CANCELLED"));
        verify(orders).cancel("order-id");
    }
}
